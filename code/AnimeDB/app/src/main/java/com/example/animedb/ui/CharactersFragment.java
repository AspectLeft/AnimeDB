package com.example.animedb.ui;

import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Character;
import com.example.animedb.presenter.CharactersAdapter;
import com.example.animedb.util.SqlUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fang on 2017/6/6.
 *
 */

public class CharactersFragment extends Fragment {
    private static final String TAG = "CharactersFragment";
    private static final int MSG_DB = 0;

    private CharactersHandler handler;


    private AnimeDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private RecyclerView recyclerView;
    private CharactersAdapter adapter;

    private List<Character> characterList = new ArrayList<>();


    private int filter_sex_position = 0;
    private String filter_sex = "";

    private String keyword = "";

    private int subjectId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new AnimeDBHelper(getActivity());
        try {
            dbHelper.createDatabase();
            dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        subjectId = getArguments().getInt("subjectId");

        handler = new CharactersHandler(this);

        freshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characters, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_characters);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CharactersAdapter(characterList);
        recyclerView.setAdapter(adapter);
        if (getActivity() instanceof MainActivity) {
            recyclerView.addOnScrollListener(((MainActivity) getActivity()).getPresenter());
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() == characterList.size() - 1) {
                    //Bottom
                    //Log.d(TAG, "load more.");
                    loadMore();

                }
            }
        });

        return view;
    }

    private void initList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initList");
                characterList.clear();
                Character character;

                String where = "1 ";
                if (subjectId > 0) {
                    where += "AND (id IN (SELECT characterId id FROM Star " +
                            "WHERE Star.subjectId = " + subjectId +
                            " ))";
                }
                switch (filter_sex_position) {
                    case 0:
                        break;
                    case 3:
                        where += "AND (sex = '') ";
                        break;
                    default:
                        where += "AND (sex LIKE '%" + filter_sex + "%') ";
                }

                if (!keyword.equals("")) {
                    where += "AND ((characterName LIKE '%" + keyword + "%') " +
                            "OR (nameCHS LIKE '%" + keyword + "%') " +
                            "OR (alias LIKE '%" + keyword + "%') " +
                            "OR (detail LIKE '%" + keyword + "%')) ";
                }


                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }

                db = dbHelper.getReadableDatabase();
                cursor = db.query("Characters", null, where, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int i = 0;
                    do {
                        character = new Character();
                        character.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        character.setCharacterName(cursor.getString(cursor.getColumnIndex("characterName")));
                        character.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
                        character.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                        character.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                        String job = "";
                        if (subjectId > 0) {
                            Cursor cursor1 = db.query("Star", null, "subjectId = ? AND characterId = ?",
                                    new String[]{String.valueOf(subjectId), String.valueOf(character.getId())},
                                    null, null, null);

                            if (cursor1.moveToFirst()) {
                                do {
                                    job += cursor1.getString(cursor1.getColumnIndex("job")) + " ";
                                } while (cursor1.moveToNext());
                            }

                            cursor1.close();
                        }
                        character.setJob(job);

                        characterList.add(character);
                        i++;
                    } while (cursor.moveToNext() && i < 50);
                }




                //cursor.close();
                //db.close();

                Message message = new Message();
                message.what = MSG_DB;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void loadMore() {
        Character character;
        if (!cursor.isAfterLast()) {
            int i = 0;
            do {
                character = new Character();
                character.setId(cursor.getInt(cursor.getColumnIndex("id")));
                character.setCharacterName(cursor.getString(cursor.getColumnIndex("characterName")));
                character.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
                character.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                character.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                String job = "";
                if (subjectId > 0) {
                    Cursor cursor1 = db.query("Star", null, "subjectId = ? AND characterId = ?",
                            new String[]{String.valueOf(subjectId), String.valueOf(character.getId())},
                            null, null, null);

                    if (cursor1.moveToFirst()) {
                        do {
                            job += cursor1.getString(cursor1.getColumnIndex("job")) + " ";
                        } while (cursor1.moveToNext());
                    }

                    cursor1.close();
                }
                character.setJob(job);

                characterList.add(character);
                i++;
            } while (cursor.moveToNext() && i < 50);
        }
        else {
            return;
        }
        if (cursor.isAfterLast()) {
            cursor.close();
            db.close();
        }
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }

    public void freshList() {

        initList();
    }

    private void freshView() {
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }

    public static CharactersFragment newInstance(int subjectId) {
        CharactersFragment fragment = new CharactersFragment();
        Bundle args = new Bundle();
        args.putInt("subjectId", subjectId);
        fragment.setArguments(args);
        return fragment;
    }

    public void showSearchBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_characters_search, null);
        final EditText editText = (EditText) layout.findViewById(R.id.characters_search_keyword);
        editText.setText(keyword);

        builder.setView(layout)
                .setTitle(R.string.search)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyword = SqlUtil.sqltext(editText.getText().toString());
                        freshList();
                    }
                });
        final AlertDialog dialog = builder.create();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    keyword = SqlUtil.sqltext(editText.getText().toString());
                    freshList();
                    dialog.dismiss();
                }

                return true;
            }
        });

        dialog.show();
    }

    public void filter() {
        final View dialogView = View.inflate(getContext(), R.layout.dialog_characters_filter, null);

        final Spinner sex = (Spinner) dialogView.findViewById(R.id.characters_filter_sex);
        sex.setSelection(filter_sex_position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setTitle(R.string.filter)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter_sex_position = sex.getSelectedItemPosition();
                        filter_sex = (String) sex.getSelectedItem();

                        initList();
                    }
                })
                .show();

    }

    private static class CharactersHandler extends Handler {
        private WeakReference<CharactersFragment> fragmentWeakReference;

        CharactersHandler(CharactersFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    CharactersFragment fragment = fragmentWeakReference.get();
                    fragment.freshView();
                    break;
                default:
            }
        }
    }
}
