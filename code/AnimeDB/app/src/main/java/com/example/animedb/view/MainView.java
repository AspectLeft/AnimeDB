package com.example.animedb.view;

import com.example.animedb.presenter.MainPresenter;

/**
 * Created by Fang on 2017/5/14.
 *
 */

public interface MainView extends BaseView {
    void showSearchBar();
    MainPresenter getPresenter();
    void changeFab(int dy);
}
