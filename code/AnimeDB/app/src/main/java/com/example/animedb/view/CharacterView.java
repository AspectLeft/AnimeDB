package com.example.animedb.view;

import com.example.animedb.model.AnimeDBHelper;

/**
 * Created by Fang on 2017/6/8.
 *
 */

public interface CharacterView extends BaseView {

    boolean getFavorited();

    AnimeDBHelper getDBHelper();

    void setFavorited(boolean favorited);
}
