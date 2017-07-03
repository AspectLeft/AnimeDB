package com.example.animedb.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.example.animedb.model.Subject;
import com.example.animedb.presenter.SubjectsAdapter;
import com.example.animedb.util.SqlUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fang on 2017/5/14.
 *
 */

public class SubjectsFragment extends Fragment {
    private AnimeDBHelper dbHelper;

    private SubjectsHandler handler;

    private static final int MSG_DB = 0;

    private RecyclerView recyclerView;
    private SubjectsAdapter adapter;

    private List<Subject> subjectList = new ArrayList<>();

    private int filter_type_position = 0;
    private String filter_type = " ";

    private int filter_sort_position = 0;

    private String keyword = "";

    private int personId = 0;
    private int characterId = 0;
    private String tagName = "";

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

        if (getArguments() != null) {
            personId = getArguments().getInt("personId");
            characterId = getArguments().getInt("characterId");
            tagName = getArguments().getString("tagName");
        }
        handler = new SubjectsHandler(this);

        freshList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_subjects);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SubjectsAdapter(subjectList);
        recyclerView.setAdapter(adapter);
        if (getActivity() instanceof MainActivity) {
            recyclerView.addOnScrollListener(((MainActivity) getActivity()).getPresenter());
        }

        return view;
    }

    private void initList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                subjectList.clear();
                Subject subject;

                String where = "1 ";
                if (!keyword.equals("")) {
                    where += "AND ((title LIKE '%" + keyword + "%') " +
                            "OR (titleCHS LIKE '%" + keyword + "%') " +
                            "OR (alias LIKE '%" + keyword + "%') " +
                            "OR (summary LIKE '%" + keyword + "%') " +
                            "OR (id IN (SELECT subjectId id FROM Tagged " +
                            "WHERE (tagName LIKE '%" + keyword + "%'))))";
                }
                if (characterId > 0 && personId > 0) {
                    where += "AND (id IN (SELECT subjectId id FROM Star " +
                            "WHERE (Star.personId = " + personId +
                            " AND Star.characterId = " + characterId +
                            " )))";
                }
                else if (personId > 0) {
                    where += "AND (id IN (SELECT subjectId id FROM Make " +
                            "WHERE Make.personId = " + personId +
                            " ))";
                }
                if (!tagName.equals("")) {
                    where += "AND (id IN (SELECT subjectId id FROM Tagged " +
                            "WHERE Tagged.tagName = '" + SqlUtil.sqltext(tagName) +
                            "'))";
                }
                switch (filter_type_position) {
                    case 0:
                        break;
                    case 5:
                        where += "AND (subjectType = '') ";
                        break;
                    default:
                        where += "AND (subjectType = '" + filter_type + "') ";
                        break;
                }

                String orderBy = "rank asc";
                switch (filter_sort_position) {
                    case 0:
                        orderBy = "rank asc";
                        break;
                    case 1:
                        orderBy = "releaseDate desc";
                        break;
                    case 2:
                        orderBy = "title asc";
                        break;
                }

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("Subject", null, where, null, null, null, orderBy);

                if (cursor.moveToFirst()) {
                    do {
                        subject = new Subject();
                        subject.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        subject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        subject.setTitleCHS(cursor.getString(cursor.getColumnIndex("titleCHS")));
                        subject.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                        subject.setSubjectType(cursor.getString(cursor.getColumnIndex("subjectType")));

                        String duty = "";
                        if (characterId > 0 && personId > 0) {
                            Cursor cursor1 = db.query("Star", null, "subjectId = ? AND characterId = ? AND personId = ?",
                                    new String[]{String.valueOf(subject.getId()), String.valueOf(characterId), String.valueOf(personId)},
                                    null, null, null);
                            if (cursor1.moveToFirst()) {
                                duty = cursor1.getString(cursor1.getColumnIndex("job")) + " ";
                            }

                            cursor1.close();
                        }
                        else if (personId > 0) {
                            Cursor cursor1 = db.query("Make", null, "subjectId = ? AND personId = ?",
                                    new String[]{String.valueOf(subject.getId()), String.valueOf(personId)},
                                    null, null, null);

                            if (cursor1.moveToFirst()) {
                                do {
                                    duty += cursor1.getString(cursor1.getColumnIndex("duty")) + " ";
                                } while (cursor1.moveToNext());
                            }

                            cursor1.close();
                        }
                        subject.setDuty(duty);

                        subjectList.add(subject);
                    } while (cursor.moveToNext());
                }

                cursor.close();

                db.close();
                Message message = new Message();
                message.what = MSG_DB;
                handler.sendMessage(message);
            }
        }).start();

    }

    public void filter() {
        final View dialogView = View.inflate(getContext(), R.layout.dialog_subjects_filter, null);
        final Spinner type = (Spinner) dialogView.findViewById(R.id.subjects_filter_type);
        type.setSelection(filter_type_position);
        final Spinner sort = (Spinner) dialogView.findViewById(R.id.subjects_filter_sort);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.filter)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter_type_position = type.getSelectedItemPosition();
                        filter_type = (String) type.getSelectedItem();

                        filter_sort_position = sort.getSelectedItemPosition();

                        initList();
                    }
                })
                .show();

    }

    public void showSearchBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_subjects_search, null);
        final EditText editText = (EditText) layout.findViewById(R.id.subjects_search_keyword);
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

    public void freshList() {
        Activity activity = getActivity();
        if (activity instanceof WorksActivity) {
            ((WorksActivity) activity).showProgressBar();
        }
        if (activity instanceof SubjectsWithTagActivity) {
            ((SubjectsWithTagActivity) activity).showProgressBar();
        }
        initList();
    }

    private void freshView() {
        Activity activity = getActivity();
        if (activity instanceof WorksActivity) {
            ((WorksActivity) activity).hideProgressBar();
        }
        if (activity instanceof SubjectsWithTagActivity) {
            ((SubjectsWithTagActivity) activity).hideProgressBar();
        }
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    public static SubjectsFragment newInstance(int personId, int characterId, String tagName) {
        SubjectsFragment subjectsFragment = new SubjectsFragment();
        Bundle args = new Bundle();
        args.putInt("personId", personId);
        args.putInt("characterId", characterId);
        args.putString("tagName", tagName);
        subjectsFragment.setArguments(args);
        return subjectsFragment;
    }

    private static class SubjectsHandler extends Handler {
        private WeakReference<SubjectsFragment> fragmentWeakReference;

        SubjectsHandler(SubjectsFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    SubjectsFragment fragment = fragmentWeakReference.get();
                    fragment.freshView();
                    break;
                default:
            }
        }
    }

}
