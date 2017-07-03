package com.example.animedb.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Character;
import com.example.animedb.model.Favorite;
import com.example.animedb.model.Person;
import com.example.animedb.model.Subject;
import com.example.animedb.presenter.FavoritesAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fang on 2017/5/21.
 *
 */

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FavoritesFragment";

    private AnimeDBHelper dbHelper;

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;

    private List<Favorite> favoriteList = new ArrayList<>();

    private int filter_type_position = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new AnimeDBHelper(getContext());
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_favorites);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavoritesAdapter(favoriteList, dbHelper);
        recyclerView.setAdapter(adapter);
        if (getActivity() instanceof MainActivity) {
            recyclerView.addOnScrollListener(((MainActivity) getActivity()).getPresenter());
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        freshList();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private void initList() {
        favoriteList.clear();
        Favorite favorite;

        String where = "1 ";
        if (filter_type_position > 0) {
            where += "AND type = " + (filter_type_position - 1);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Favorites", null, where, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                favorite = new Favorite();
                favorite.setId(cursor.getInt(cursor.getColumnIndex("id")));
                favorite.setType(cursor.getInt(cursor.getColumnIndex("type")));
                favorite.setObjectId(cursor.getInt(cursor.getColumnIndex("objectId")));
                favorite.setMark(cursor.getInt(cursor.getColumnIndex("mark")));
                favorite.setComment(cursor.getString(cursor.getColumnIndex("comment")));
                switch (favorite.getType()) {
                    case Favorite.TYPE_SUBJECT:
                        Cursor cursorToSubject = db.query("Subject", null, "id = " + String.valueOf(favorite.getObjectId()), null, null, null, null);

                        if (cursorToSubject.moveToFirst()) {
                            Subject subject = new Subject();
                            subject.setId(cursorToSubject.getInt(cursorToSubject.getColumnIndex("id")));
                            subject.setTitle(cursorToSubject.getString(cursorToSubject.getColumnIndex("title")));
                            subject.setTitleCHS(cursorToSubject.getString(cursorToSubject.getColumnIndex("titleCHS")));
                            subject.setPictureUrl(cursorToSubject.getString(cursorToSubject.getColumnIndex("pictureUrl")));
                            subject.setSubjectType(cursorToSubject.getString(cursorToSubject.getColumnIndex("subjectType")));

                            favorite.setSubject(subject);
                        }


                        cursorToSubject.close();
                        break;
                    case Favorite.TYPE_PERSON:
                        Cursor cursorToPerson = db.query("Person", null, "id = " + String.valueOf(favorite.getObjectId()), null, null, null, null);
                        if (cursorToPerson.moveToFirst()) {
                            Person person = new Person();
                            person.setId(cursorToPerson.getInt(cursorToPerson.getColumnIndex("id")));
                            person.setPersonName(cursorToPerson.getString(cursorToPerson.getColumnIndex("personName")));
                            person.setNameCHS(cursorToPerson.getString(cursorToPerson.getColumnIndex("nameCHS")));
                            person.setAlias(cursorToPerson.getString(cursorToPerson.getColumnIndex("alias")));
                            person.setJob(cursorToPerson.getString(cursorToPerson.getColumnIndex("job")));
                            person.setSex(cursorToPerson.getString(cursorToPerson.getColumnIndex("sex")));
                            person.setPictureUrl(cursorToPerson.getString(cursorToPerson.getColumnIndex("pictureUrl")));
                            person.setBirthday(cursorToPerson.getString(cursorToPerson.getColumnIndex("birthday")));
                            person.setDetail(cursorToPerson.getString(cursorToPerson.getColumnIndex("detail")));

                            favorite.setPerson(person);
                        }


                        cursorToPerson.close();
                        break;
                    case Favorite.TYPE_CHARACTER:
                        Cursor cursorToCharacter = db.query("Characters", null, "id = " + String.valueOf(favorite.getObjectId()), null, null, null, null);
                        if (cursorToCharacter.moveToFirst()) {
                            Character character = new Character();
                            character.setId(cursorToCharacter.getInt(cursorToCharacter.getColumnIndex("id")));
                            character.setCharacterName(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("characterName")));
                            character.setNameCHS(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("nameCHS")));
                            character.setAlias(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("alias")));
                            character.setSex(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("sex")));
                            character.setDetail(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("detail")));
                            character.setPictureUrl(cursorToCharacter.getString(cursorToCharacter.getColumnIndex("pictureUrl")));

                            favorite.setCharacter(character);
                        }
                        cursorToCharacter.close();
                        break;

                }

                favoriteList.add(favorite);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    public void freshList() {
        initList();
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    public void filter() {
        final View dialogView = View.inflate(getContext(), R.layout.dialog_favorites_filter, null);
        final Spinner type = (Spinner) dialogView.findViewById(R.id.favorite_filter_type);
        type.setSelection(filter_type_position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.filter)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter_type_position = type.getSelectedItemPosition();

                        freshList();
                    }
                })
                .show();
    }
}
