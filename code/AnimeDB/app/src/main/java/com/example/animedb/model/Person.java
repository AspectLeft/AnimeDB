package com.example.animedb.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Fang on 2017/5/23.
 *
 */

public class Person implements Entity {
    private int id;
    private String personName;
    private String nameCHS;
    private String alias;
    private String pictureUrl;
    private String sex;
    private String birthday;
    private String job;
    private String detail;

    private String duty; //Make

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    @Override
    public void addToFavorite(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", Favorite.TYPE_PERSON);
        values.put("objectId", id);
        values.put("mark", 0); //null
        values.put("comment", "备注");
        db.insert("Favorites", null, values);
        db.close();
    }

    @Override
    public void removeFromFavorites(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("Favorites", "type = ? AND objectId = ?", new String[]{String.valueOf(Favorite.TYPE_PERSON), String.valueOf(id)});
        db.close();
    }
}
