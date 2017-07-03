package com.example.animedb.presenter;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.Person;
import com.example.animedb.view.PersonView;

/**
 * Created by Fang on 2017/5/29.
 *
 */

public class PersonPresenter implements Presenter<PersonView>, View.OnClickListener {
    private PersonView personView;
    private Person person;

    public PersonPresenter(PersonView view, Person person) {
        attachView(view);
        this.person = person;
    }

    @Override
    public void attachView(PersonView view) {
        this.personView = view;
    }

    @Override
    public void detachView() {
        if (personView != null) {
            personView = null;
        }
        if (person != null) {
            person = null;
        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        TextView textView;
        switch (v.getId()) {
            case R.id.fab_person:
                if (personView.getFavorited()) {
                    person.removeFromFavorites(personView.getDBHelper());
                    personView.setFavorited(false);
                }
                else {
                    person.addToFavorite(personView.getDBHelper());
                    personView.setFavorited(true);
                }

                break;
            case R.id.layout_person_alias:
                String alias = person.getAlias();
                textView = new TextView(personView.getContext());
                textView.setText(alias.substring(0, alias.length() - 1));
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(personView.getContext());
                builder.setTitle(R.string.alias)
                        .setView(textView)
                        .show();
                break;
            case R.id.layout_person_detail:
                String detail = person.getDetail();
                textView = new TextView(personView.getContext());
                textView.setText(detail);
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(personView.getContext());
                builder.setTitle(R.string.summary)
                        .setView(textView)
                        .show();
                break;
            default:
        }
    }
}
