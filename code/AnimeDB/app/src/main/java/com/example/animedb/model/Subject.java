package com.example.animedb.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Fang on 2017/5/14.
 *
 */

public class Subject implements Entity {
    private static final int MARK_WISHES = 0;

    private int id;
    private String title;
    private String titleCHS;
    private String subjectType;
    private String pictureUrl;
    private int epNum;
    private String alias;
    private double score;
    private int rank;
    private int[] scores = new int[11];
    private String releaseDate;
    private String summary;

    private String duty;

    private List<Person> dubbers;   //for character

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleCHS() {
        return titleCHS;
    }

    public void setTitleCHS(String titleCHS) {
        this.titleCHS = titleCHS;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getEpNum() {
        return epNum;
    }

    public void setEpNum(int epNum) {
        this.epNum = epNum;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int[] getScores() {
        return scores;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public void addToFavorite(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", Favorite.TYPE_SUBJECT);
        values.put("objectId", id);
        values.put("mark", MARK_WISHES);
        values.put("comment", "备注");
        db.insert("Favorites", null, values);
        db.close();
    }

    @Override
    public void removeFromFavorites(AnimeDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("Favorites", "type = ? AND objectId = ?", new String[]{String.valueOf(Favorite.TYPE_SUBJECT), String.valueOf(id)});
        db.close();
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public List<Person> getDubbers() {
        return dubbers;
    }

    public void setDubbers(List<Person> dubbers) {
        this.dubbers = dubbers;
    }
}
