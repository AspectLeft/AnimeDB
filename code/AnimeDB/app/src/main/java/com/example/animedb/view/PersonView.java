package com.example.animedb.view;

import com.example.animedb.model.AnimeDBHelper;

/**
 * Created by Fang on 2017/5/29.
 *
 */

public interface PersonView extends BaseView {

    boolean getFavorited();

    AnimeDBHelper getDBHelper();

    void setFavorited(boolean favorited);
}
