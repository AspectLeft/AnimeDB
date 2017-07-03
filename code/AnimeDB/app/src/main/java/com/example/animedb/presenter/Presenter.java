package com.example.animedb.presenter;

/**
 * Created by Fang on 2017/5/14.
 * P of MVP
 */

interface Presenter<V> {
    void attachView(V view);
    void detachView();
}
