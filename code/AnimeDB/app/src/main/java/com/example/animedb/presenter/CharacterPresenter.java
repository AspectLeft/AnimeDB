package com.example.animedb.presenter;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.Character;
import com.example.animedb.view.CharacterView;

/**
 * Created by Fang on 2017/6/8.
 *
 */

public class CharacterPresenter implements Presenter<CharacterView>, View.OnClickListener {
    private CharacterView characterView;
    private Character character;

    public CharacterPresenter(CharacterView view, Character character) {
        attachView(view);
        this.character = character;
    }

    @Override
    public void attachView(CharacterView view) {
        this.characterView = view;
    }

    @Override
    public void detachView() {
        if (characterView != null) {
            characterView = null;
        }
        if (character != null) {
            character = null;
        }

    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        TextView textView;
        switch (v.getId()) {
            case R.id.fab_character:
                if (characterView.getFavorited()) {
                    character.removeFromFavorites(characterView.getDBHelper());
                    characterView.setFavorited(false);
                }
                else {
                    character.addToFavorite(characterView.getDBHelper());
                    characterView.setFavorited(true);
                }
                break;
            case R.id.layout_character_alias:
                String alias = character.getAlias();
                textView = new TextView(characterView.getContext());
                textView.setText(alias.substring(0, alias.length() - 1));
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(characterView.getContext());
                builder.setTitle(R.string.alias)
                        .setView(textView)
                        .show();
                break;
            case R.id.layout_character_detail:
            case R.id.layout_person_detail:
                String detail = character.getDetail();
                textView = new TextView(characterView.getContext());
                textView.setText(detail);
                textView.setTextIsSelectable(true);
                textView.setPadding(40, 40, 40, 40);
                builder = new AlertDialog.Builder(characterView.getContext());
                builder.setTitle(R.string.summary)
                        .setView(textView)
                        .show();
                break;
        }
    }
}
