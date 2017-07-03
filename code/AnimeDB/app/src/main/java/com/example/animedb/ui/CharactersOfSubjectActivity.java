package com.example.animedb.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Character;
import com.example.animedb.model.Person;
import com.example.animedb.presenter.CharactersOfSubjectAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CharactersOfSubjectActivity extends AppCompatActivity {
    private static final String TAG = "COSActivity";
    private static final int MSG_DB = 0;
    private int subjectId;

    private AnimeDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private CoSHandler handler;

    private RecyclerView recyclerView;
    private CharactersOfSubjectAdapter adapter;


    private List<Character> characterList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters_of_subject);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_characters_of_subject);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.character);
        }

        subjectId = getIntent().getIntExtra("subjectId", 0);
        handler = new CoSHandler(this);

        dbHelper = new AnimeDBHelper(this);
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_characters_of_subject);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CharactersOfSubjectAdapter(characterList, subjectId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initList");
                characterList.clear();
                Character character;

                String where = "1 ";
                if (subjectId > 0) {
                    where += "AND (id IN (SELECT characterId id FROM Star " +
                            "WHERE Star.subjectId = " + subjectId +
                            " ))";
                }

                db = dbHelper.getReadableDatabase();
                cursor = db.query("Characters", null, where, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        character = new Character();
                        character.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        character.setCharacterName(cursor.getString(cursor.getColumnIndex("characterName")));
                        character.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
                        character.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                        character.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                        String job = "";
                        if (subjectId > 0) {
                            Cursor cursor1 = db.query("Star", null, "subjectId = ? AND characterId = ?",
                                    new String[]{String.valueOf(subjectId), String.valueOf(character.getId())},
                                    null, null, null);

                            if (cursor1.moveToFirst()) {
                                job = cursor1.getString(cursor1.getColumnIndex("job"));
                            }


                            cursor1.close();

                            Person dubber;
                            List<Person> dubbers = new ArrayList<>();
                            Cursor cursorToDubbers = db.query("Person", null, "id IN (SELECT personId id FROM Star " +
                                            "WHERE (Star.subjectId = ? AND Star.characterId = ?))",
                                    new String[]{String.valueOf(subjectId), String.valueOf(character.getId())}, null, null, null);
                            if (cursorToDubbers.moveToFirst()) {
                                do {
                                    dubber = new Person();
                                    dubber.setId(cursorToDubbers.getInt(cursorToDubbers.getColumnIndex("id")));

                                    dubber.setPictureUrl(cursorToDubbers.getString(cursorToDubbers.getColumnIndex("pictureUrl")));
                                    dubber.setPersonName(cursorToDubbers.getString(cursorToDubbers.getColumnIndex("personName")));

                                    dubbers.add(dubber);

                                } while (cursorToDubbers.moveToNext());
                            }
                            character.setDubbers(dubbers);
                            Log.d(TAG, String.valueOf(dubbers.size()));


                            cursorToDubbers.close();

                        }
                        character.setJob(job);

                        characterList.add(character);
                    } while (cursor.moveToNext());
                }


                cursor.close();
                db.close();

                Message message = new Message();
                message.what = MSG_DB;
                handler.sendMessage(message);
            }
        }).start();
    }

    public void freshView() {
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }


    private static class CoSHandler extends Handler {
        private WeakReference<CharactersOfSubjectActivity> activityWeakReference;

        CoSHandler(CharactersOfSubjectActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    CharactersOfSubjectActivity activity = activityWeakReference.get();
                    activity.freshView();

                    break;
            }
        }
    }
}
