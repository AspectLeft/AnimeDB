package com.example.animedb.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Favorite;
import com.example.animedb.model.Subject;
import com.example.animedb.presenter.SubjectPresenter;
import com.example.animedb.view.SubjectView;

public class SubjectActivity extends AppCompatActivity implements SubjectView {

    private int subjectId;
    private Subject subject;

    private AnimeDBHelper dbHelper;

    private FloatingActionButton floatingActionButton;

    public boolean favorited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);



        Intent intent = getIntent();
        subjectId = intent.getIntExtra("id", 0);

        dbHelper = new AnimeDBHelper(this);
        dbHelper.openDataBase();

        initData();

        SubjectPresenter subjectPresenter = new SubjectPresenter(this, subject);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subject);
        ImageView view_subjectImage = (ImageView) findViewById(R.id.image_subject);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(subject.getTitle());
        }


        TextView view_subjectTitleCHS = (TextView) findViewById(R.id.subject_titleCHS);
        if (subject.getTitleCHS().equals("")) {
            LinearLayout parentLayout = (LinearLayout) findViewById(R.id.layout_subject_titleCHS);
            parentLayout.setVisibility(View.GONE);
        }
        else {
            view_subjectTitleCHS.setText(subject.getTitleCHS());
        }

        TextView view_subjectType = (TextView) findViewById(R.id.subject_type);
        if (subject.getSubjectType().equals("")) {
            LinearLayout parentLayout = (LinearLayout) findViewById(R.id.layout_subject_type);
            parentLayout.setVisibility(View.GONE);
        }
        else {
            view_subjectType.setText(subject.getSubjectType());
        }

        TextView view_subjectEpNum = (TextView) findViewById(R.id.subject_epNum);
        if (subject.getEpNum() == 0) {
            view_subjectEpNum.setText(R.string.unknown);
        }
        else {
            view_subjectEpNum.setText(String.valueOf(subject.getEpNum()));
        }

        TextView view_subjectAlias = (TextView) findViewById(R.id.subject_alias);
        LinearLayout layout_subject_alias = (LinearLayout) findViewById(R.id.layout_subject_alias);
        if (subject.getAlias().equals("")) {
            layout_subject_alias.setVisibility(View.GONE);
        }
        else {
            String alias = subject.getAlias();
            view_subjectAlias.setText(alias.substring(0, alias.length() - 1));
        }
        layout_subject_alias.setOnClickListener(subjectPresenter);

        LinearLayout layout_subject_score = (LinearLayout) findViewById(R.id.layout_subject_score);
        TextView view_subjectScore = (TextView) findViewById(R.id.subject_score);
        view_subjectScore.setText(String.valueOf(subject.getScore()));
        layout_subject_score.setOnClickListener(subjectPresenter);

        TextView view_subjectRank = (TextView) findViewById(R.id.subject_rank);
        view_subjectRank.setText(String.valueOf(subject.getRank()));

        TextView view_subjectReleaseDate = (TextView) findViewById(R.id.subject_releaseDate);
        view_subjectReleaseDate.setText(subject.getReleaseDate());

        TextView view_subjectSummary = (TextView) findViewById(R.id.subject_summary);

        LinearLayout layout_subject_summary = (LinearLayout) findViewById(R.id.layout_subject_summary);
        if (subject.getSummary().equals("")) {
            layout_subject_summary.setVisibility(View.GONE);
        }
        else {
            view_subjectSummary.setText(subject.getSummary());
        }
        layout_subject_summary.setOnClickListener(subjectPresenter);



        String pictureUrl = subject.getPictureUrl();
        if (!pictureUrl.equals("")) {

            Glide.with(this).load("http:" + pictureUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view_subjectImage);

        }

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_subject);
        if (favorited) {
            floatingActionButton.setImageResource(R.drawable.ic_favorite_dark);
        }
        floatingActionButton.setOnClickListener(subjectPresenter);


    }

    private void initData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Subject", null, "id = ?", new String[] {String.valueOf(subjectId)}, null, null, null);
        if (cursor.moveToFirst()) {
            subject = new Subject();
            subject.setId(subjectId);
            subject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            subject.setTitleCHS(cursor.getString(cursor.getColumnIndex("titleCHS")));
            subject.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
            subject.setSubjectType(cursor.getString(cursor.getColumnIndex("subjectType")));
            subject.setEpNum(cursor.getInt(cursor.getColumnIndex("epNum")));
            subject.setAlias(cursor.getString(cursor.getColumnIndex("alias")));
            subject.setScore(cursor.getDouble(cursor.getColumnIndex("score")));
            subject.setRank(cursor.getInt(cursor.getColumnIndex("rank")));
            subject.setReleaseDate(cursor.getString(cursor.getColumnIndex("releaseDate")));
            subject.setSummary(cursor.getString(cursor.getColumnIndex("summary")));

            int[] scores = new int[11];
            for (int i = 1; i <= 10; ++i) {
                scores[i] = cursor.getInt(cursor.getColumnIndex("score" + i));
            }
            subject.setScores(scores);

            Cursor cursor1 = db.query("Favorites", null, "type = ? AND objectId = ?",
                    new String[]{String.valueOf(Favorite.TYPE_SUBJECT), String.valueOf(subject.getId())}, null, null, null);
            favorited = cursor1.moveToFirst();
            cursor1.close();

            //TODO
        }



        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.subject_menu_makers:
                intent = new Intent(SubjectActivity.this, MakersActivity.class);
                intent.putExtra("subjectId", subjectId);
                startActivity(intent);
                return true;
            case R.id.subject_menu_characters:
                intent = new Intent(SubjectActivity.this, CharactersOfSubjectActivity.class);
                intent.putExtra("subjectId", subjectId);
                startActivity(intent);
                return true;
            case R.id.subject_menu_labels:
                intent = new Intent(SubjectActivity.this, TagsOfSubjectActivity.class);
                intent.putExtra("subjectId", subjectId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    @Override
    public boolean getFavorited() {
        return favorited;
    }

    @Override
    public AnimeDBHelper getDBHelper() {
        return dbHelper;
    }

    @Override
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
        if (favorited) {
            floatingActionButton.setImageResource(R.drawable.ic_favorite_dark);
        }
        else {
            floatingActionButton.setImageResource(R.drawable.ic_favorite_border_dark);
        }
    }
}
