package com.example.animedb.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.view.MainView;

import java.io.IOException;

/**
 * Created by Fang on 2017/5/14.
 *
 */

public class MainPresenter extends RecyclerView.OnScrollListener implements Presenter<MainView>, View.OnClickListener {
    private MainView mainView;


    public MainPresenter(MainView v){
        attachView(v);
        AnimeDBHelper dbHelper = new AnimeDBHelper(v.getContext());
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
            dbHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void attachView(MainView view) {
        this.mainView = view;

    }

    @Override
    public void detachView() {
        if (mainView != null) {
            mainView = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                mainView.showSearchBar();

                break;
            default:
        }
    }

    @Override
    public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
        mainView.changeFab(dy);
    }
}
