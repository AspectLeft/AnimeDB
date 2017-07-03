package com.example.animedb.model;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Fang on 2017/5/21.
 *
 */

public class Favorite {
    public static final int TYPE_SUBJECT = 0;
    public static final int TYPE_PERSON = 1;
    public static final int TYPE_CHARACTER = 2;

    private int id;
    private int type;
    private int objectId;
    private int mark;
    private String comment;

    private Subject subject = null;
    private Person person = null;
    private Character character = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void removeFromFavorites(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("Favorites", "id = " + id, null);
        db.close();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}
