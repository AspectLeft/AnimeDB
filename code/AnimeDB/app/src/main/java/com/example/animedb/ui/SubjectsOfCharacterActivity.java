package com.example.animedb.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Person;
import com.example.animedb.model.Subject;
import com.example.animedb.presenter.SubjectsOfCharacterAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SubjectsOfCharacterActivity extends AppCompatActivity {
    private static final String TAG = "SoCActivity";
    private static final int MSG_DB = 0;
    private int characterId;


    private AnimeDBHelper dbHelper;

    private SoCHandler handler;

    private RecyclerView recyclerView;
    private SubjectsOfCharacterAdapter adapter;

    private List<Subject> subjectList = new ArrayList<>();


    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_of_character);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subjects_of_character);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.star);
        }

        characterId = getIntent().getIntExtra("characterId", 0);
        handler = new SoCHandler(this);

        dbHelper = new AnimeDBHelper(this);
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        initData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_subjects_of_character);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SubjectsOfCharacterAdapter(subjectList, characterId);
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
                subjectList.clear();
                Subject subject;

                String where = "1 ";
                if (characterId > 0) {
                    where += "AND (id IN (SELECT subjectId id FROM Star " +
                            "WHERE (Star.characterId = " + characterId +
                            " )))";
                }



                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("Subject", null, where, null, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        subject = new Subject();
                        subject.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        subject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        subject.setTitleCHS(cursor.getString(cursor.getColumnIndex("titleCHS")));
                        subject.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                        subject.setSubjectType(cursor.getString(cursor.getColumnIndex("subjectType")));

                        String duty = "";
                        if (characterId > 0) {
                            Cursor cursor1 = db.query("Star", null, "subjectId = ? AND characterId = ?",
                                    new String[]{String.valueOf(subject.getId()), String.valueOf(characterId)},
                                    null, null, null);
                            if (cursor1.moveToFirst()) {
                                duty = cursor1.getString(cursor1.getColumnIndex("job")) + " ";
                            }

                            cursor1.close();
                        }

                        subject.setDuty(duty);

                        Person dubber;
                        List<Person> dubbers = new ArrayList<>();
                        Cursor cursorToDubbers = db.query("Person", null, "id IN (SELECT personId id FROM Star " +
                                        "WHERE (Star.subjectId = ? AND Star.characterId = ?))",
                                new String[]{String.valueOf(subject.getId()), String.valueOf(characterId)}, null, null, null);
                        if (cursorToDubbers.moveToFirst()) {
                            do {
                                dubber = new Person();
                                dubber.setId(cursorToDubbers.getInt(cursorToDubbers.getColumnIndex("id")));

                                dubber.setPictureUrl(cursorToDubbers.getString(cursorToDubbers.getColumnIndex("pictureUrl")));
                                dubber.setPersonName(cursorToDubbers.getString(cursorToDubbers.getColumnIndex("personName")));

                                dubbers.add(dubber);

                            } while (cursorToDubbers.moveToNext());
                        }
                        subject.setDubbers(dubbers);
                        Log.d(TAG, String.valueOf(dubbers.size()));


                        cursorToDubbers.close();

                        subjectList.add(subject);
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

    private static class SoCHandler extends Handler {
        private WeakReference<SubjectsOfCharacterActivity> activityWeakReference;

        SoCHandler(SubjectsOfCharacterActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    SubjectsOfCharacterActivity activity = activityWeakReference.get();
                    activity.freshView();

                    break;
            }
        }
    }

    private void freshView() {
        progressBar.setVisibility(View.GONE);
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }
}
