package com.example.animedb.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.animedb.model.Character;
import com.example.animedb.model.Favorite;
import com.example.animedb.presenter.CharacterPresenter;
import com.example.animedb.view.CharacterView;

public class CharacterActivity extends AppCompatActivity implements CharacterView {
    CharacterPresenter presenter;

    private int characterId;
    private Character character;

    private AnimeDBHelper dbHelper;

    private boolean favorited = false;

    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        Intent intent = getIntent();
        characterId = intent.getIntExtra("id", 0);

        dbHelper = new AnimeDBHelper(this);
        dbHelper.openDataBase();

        initData();

        presenter = new CharacterPresenter(this, character);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_character);
        ImageView view_characterImage = (ImageView) findViewById(R.id.image_character);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(character.getCharacterName());
        }

        String pictureUrl = character.getPictureUrl();
        if (!pictureUrl.equals("")) {

            Glide.with(this).load("http:" + pictureUrl)
                    .asBitmap()
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view_characterImage);
        }

        if (character.getNameCHS().equals("")) {
            findViewById(R.id.layout_character_nameCHS).setVisibility(View.GONE);
        }
        else {
            TextView view_characterNameCHS = (TextView) findViewById(R.id.character_nameCHS);
            view_characterNameCHS.setText(character.getNameCHS());
        }

        LinearLayout layout_character_alias = (LinearLayout) findViewById(R.id.layout_character_alias);
        if (character.getAlias().equals("")) {
            layout_character_alias.setVisibility(View.GONE);
        }
        else {
            TextView view_characterAlias = (TextView) findViewById(R.id.character_alias);
            view_characterAlias.setText(character.getAlias());
        }
        layout_character_alias.setOnClickListener(presenter);

        if (character.getSex().equals("")) {
            findViewById(R.id.layout_character_sex).setVisibility(View.GONE);
        }
        else {
            TextView view_characterSex = (TextView) findViewById(R.id.character_sex);
            view_characterSex.setText(character.getSex());
        }

        LinearLayout layout_character_detail = (LinearLayout) findViewById(R.id.layout_character_detail);
        if (character.getDetail().equals("")) {
            layout_character_detail.setVisibility(View.GONE);
        }
        else {
            TextView view_characterDetail = (TextView) findViewById(R.id.character_detail);
            view_characterDetail.setText(character.getDetail());
        }
        layout_character_detail.setOnClickListener(presenter);

        fab = (FloatingActionButton) findViewById(R.id.fab_character);
        if (favorited) {
            fab.setImageResource(R.drawable.ic_favorite_dark);
        }
        fab.setOnClickListener(presenter);

    }

    private void initData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Characters", null, "id = ?", new String[]{String.valueOf(characterId)}, null, null, null);
        if (cursor.moveToFirst()) {
            character = new Character();
            character.setId(characterId);
            character.setCharacterName(cursor.getString(cursor.getColumnIndex("characterName")));
            character.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
            character.setAlias(cursor.getString(cursor.getColumnIndex("alias")));
            character.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            character.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
            character.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));

            Cursor cursor1 = db.query("Favorites", null, "type = ? AND objectId = ?",
                    new String[]{String.valueOf(Favorite.TYPE_CHARACTER), String.valueOf(character.getId())}, null, null, null);
            favorited = cursor1.moveToFirst();
            cursor1.close();
        }

        cursor.close();
        db.close();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_character, menu);
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
            case R.id.character_menu_subjects:
                Intent intent = new Intent(CharacterActivity.this, SubjectsOfCharacterActivity.class);
                intent.putExtra("characterId", characterId);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
