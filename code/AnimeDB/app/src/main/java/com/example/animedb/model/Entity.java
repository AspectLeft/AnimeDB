package com.example.animedb.model;

/**
 * Created by Fang on 2017/5/30.
 *
 */

interface Entity {
    void addToFavorite(AnimeDBHelper dbHelper);

    void removeFromFavorites(AnimeDBHelper dbHelper);
}
