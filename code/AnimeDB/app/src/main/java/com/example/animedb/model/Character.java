package com.example.animedb.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Fang on 2017/6/6.
 *
 */

public class Character implements Entity {
    private int id;
    private String characterName;
    private String nameCHS;
    private String alias;
    private String pictureUrl;
    private String sex;
    private String detail;

    private String job;

    private List<Person> dubbers;
    private List<Subject> subjects;


    @Override
    public void addToFavorite(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", Favorite.TYPE_CHARACTER);
        values.put("objectId", id);
        values.put("mark", 0); //null
        values.put("comment", "备注");
        db.insert("Favorites", null, values);
        db.close();
    }

    @Override
    public void removeFromFavorites(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("Favorites", "type = ? AND objectId = ?", new String[]{String.valueOf(Favorite.TYPE_CHARACTER), String.valueOf(id)});
        db.close();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCHS() {
        return nameCHS;
    }

    public void setNameCHS(String nameCHS) {
        this.nameCHS = nameCHS;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<Person> getDubbers() {
        return dubbers;
    }

    public void setDubbers(List<Person> dubbers) {
        this.dubbers = dubbers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
