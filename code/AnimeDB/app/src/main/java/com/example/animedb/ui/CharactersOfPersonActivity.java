package com.example.animedb.ui;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Character;
import com.example.animedb.model.Subject;
import com.example.animedb.presenter.CharactersOfPersonAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CharactersOfPersonActivity extends AppCompatActivity {
    private static final String TAG = "COPActivity";
    private static final int MSG_DB = 0;
    private int personId;
    private AnimeDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private RecyclerView recyclerView;
    private CharactersOfPersonAdapter adapter;

    private CoPHandler handler;

    private List<Character> characterList = new ArrayList<>();

    private int filter_job_position = 0;
    private String filter_job = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters_of_person);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_characters_of_person);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.character);
        }

        personId = getIntent().getIntExtra("personId", 0);
        handler = new CoPHandler(this);

        dbHelper = new AnimeDBHelper(this);
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_characters_of_person);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CharactersOfPersonAdapter(characterList, personId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_characters_of_person, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.characters_of_person_menu_filter:
                filter();
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
                if (personId > 0) {
                    switch (filter_job_position ) {
                        case 0:
                            where += "AND (id IN (SELECT characterId id FROM Star " +
                                    "WHERE Star.personId = " + personId +
                                    " ))";
                            break;
                        default:
                            where += "AND (id IN (SELECT characterId id FROM Star " +
                                    "WHERE (Star.personId = " + personId +
                                    " AND Star.job = '" + filter_job +
                                    "' )))";
                    }

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
                        if (personId > 0) {
                            List<Subject> subjects = new ArrayList<>();
                            Cursor cursorToStar = db.query("Star", null, "personId = ? AND characterId = ?",
                                    new String[]{String.valueOf(personId), String.valueOf(character.getId())}, null, null, null);
                            if (cursorToStar.moveToFirst()) {
                                do {
                                    int subjectId = cursorToStar.getInt(cursorToStar.getColumnIndex("subjectId"));
                                    Subject subject = new Subject();
                                    subject.setId(subjectId);
                                    String job = cursorToStar.getString(cursorToStar.getColumnIndex("job"));
                                    subject.setDuty(job);
                                    Cursor cursorToSubject = db.query("Subject", null, "id = " + subjectId, null, null, null, null);
                                    if (cursorToSubject.moveToFirst()) {
                                        subject.setTitle(cursorToSubject.getString(cursorToSubject.getColumnIndex("title")));
                                        subject.setPictureUrl(cursorToSubject.getString(cursorToSubject.getColumnIndex("pictureUrl")));
                                    }
                                    cursorToSubject.close();
                                    if (filter_job_position == 0 || subject.getDuty().equals(filter_job))
                                        subjects.add(subject);
                                } while (cursorToStar.moveToNext());
                            }
                            cursorToStar.close();
                            character.setSubjects(subjects);
                        }

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

    private void filter() {
        final View dialogView = View.inflate(this, R.layout.dialog_characters_of_person_filter, null);
        final Spinner job = (Spinner) dialogView.findViewById(R.id.characters_of_person_filter_job);
        job.setSelection(filter_job_position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filter)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter_job_position = job.getSelectedItemPosition();
                        filter_job = (String) job.getSelectedItem();
                        initData();
                    }
                })
                .show();

    }


    private static class CoPHandler extends Handler {
        private WeakReference<CharactersOfPersonActivity> activityWeakReference;

        CoPHandler(CharactersOfPersonActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    CharactersOfPersonActivity activity = activityWeakReference.get();
                    activity.freshView();

                    break;
            }
        }
    }

    private void freshView() {
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }
}
