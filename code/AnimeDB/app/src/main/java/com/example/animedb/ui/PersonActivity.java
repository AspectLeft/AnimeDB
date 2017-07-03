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
import com.example.animedb.model.Person;
import com.example.animedb.presenter.PersonPresenter;
import com.example.animedb.view.PersonView;

public class PersonActivity extends AppCompatActivity implements PersonView {

    private int personId;
    private Person person;

    private AnimeDBHelper dbHelper;

    private boolean favorited = false;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        personId = intent.getIntExtra("id", 0);

        dbHelper = new AnimeDBHelper(this);
        dbHelper.openDataBase();

        initData();

        PersonPresenter personPresenter = new PersonPresenter(this, person);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_person);
        ImageView view_personImage = (ImageView) findViewById(R.id.image_person);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(person.getPersonName());
        }

        String pictureUrl = person.getPictureUrl();
        if (!pictureUrl.equals("")) {

            Glide.with(this).load("http:" + pictureUrl)
                    .asBitmap()
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view_personImage);
        }

        if (person.getNameCHS().equals("")) {
            findViewById(R.id.layout_person_nameCHS).setVisibility(View.GONE);
        }
        else {
            TextView view_personNameCHS = (TextView) findViewById(R.id.person_nameCHS);
            view_personNameCHS.setText(person.getNameCHS());
        }

        if (person.getJob().equals("")) {
            findViewById(R.id.layout_person_job).setVisibility(View.GONE);
        }
        else {
            TextView view_personJob = (TextView) findViewById(R.id.person_job);
            view_personJob.setText(person.getJob());
        }

        LinearLayout layout_person_alias = (LinearLayout) findViewById(R.id.layout_person_alias);
        if (person.getAlias().equals("")) {
            layout_person_alias.setVisibility(View.GONE);
        }
        else {
            TextView view_personAlias = (TextView) findViewById(R.id.person_alias);
            view_personAlias.setText(person.getAlias());
        }
        layout_person_alias.setOnClickListener(personPresenter);

        if (person.getSex().equals("")) {
            findViewById(R.id.layout_person_sex).setVisibility(View.GONE);
        }
        else {
            TextView view_personSex = (TextView) findViewById(R.id.person_sex);
            view_personSex.setText(person.getSex());
        }

        if (person.getBirthday().equals("")) {
            findViewById(R.id.layout_person_birthday).setVisibility(View.GONE);
        }
        else {
            TextView view_personBirthday = (TextView) findViewById(R.id.person_birthday);
            view_personBirthday.setText(person.getBirthday());
        }

        LinearLayout layout_person_detail = (LinearLayout) findViewById(R.id.layout_person_detail);
        if (person.getDetail().equals("")) {
            layout_person_detail.setVisibility(View.GONE);
        }
        else {
            TextView view_personDetail = (TextView) findViewById(R.id.person_detail);
            view_personDetail.setText(person.getDetail());
        }
        layout_person_detail.setOnClickListener(personPresenter);

        fab = (FloatingActionButton) findViewById(R.id.fab_person);
        if (favorited) {
            fab.setImageResource(R.drawable.ic_favorite_dark);
        }
        fab.setOnClickListener(personPresenter);

    }

    private void initData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Person", null, "id = ?", new String[]{String.valueOf(personId)}, null, null, null);
        if (cursor.moveToFirst()) {
            person = new Person();
            person.setId(personId);
            person.setPersonName(cursor.getString(cursor.getColumnIndex("personName")));
            person.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
            person.setAlias(cursor.getString(cursor.getColumnIndex("alias")));
            person.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
            person.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            person.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
            person.setJob(cursor.getString(cursor.getColumnIndex("job")));
            person.setDetail(cursor.getString(cursor.getColumnIndex("detail")));

            Cursor cursor1 = db.query("Favorites", null, "type = ? AND objectId = ?",
                    new String[]{String.valueOf(Favorite.TYPE_PERSON), String.valueOf(person.getId())}, null, null, null);
            favorited = cursor1.moveToFirst();
            cursor1.close();
        }

        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_person, menu);
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
            case R.id.person_menu_subjects:
                intent = new Intent(PersonActivity.this, WorksActivity.class);
                intent.putExtra("personId", personId);
                startActivity(intent);
                return true;
            case R.id.person_menu_characters:
                intent = new Intent(PersonActivity.this, CharactersOfPersonActivity.class);
                intent.putExtra("personId", personId);
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
            fab.setImageResource(R.drawable.ic_favorite_dark);
        }
        else {
            fab.setImageResource(R.drawable.ic_favorite_border_dark);
        }
    }
}
