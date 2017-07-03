package com.example.animedb.view;

import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Subject;

/**
 * Created by Fang on 2017/5/17.
 *
 */

public interface SubjectView extends BaseView {
    Subject getSubject();

    boolean getFavorited();

    AnimeDBHelper getDBHelper();

    void setFavorited(boolean favorited);
}
