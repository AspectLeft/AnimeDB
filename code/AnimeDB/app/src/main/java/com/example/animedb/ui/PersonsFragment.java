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
import com.example.animedb.model.Person;
import com.example.animedb.presenter.PersonsAdapter;
import com.example.animedb.util.SqlUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fang on 2017/5/23.
 *
 */

public class PersonsFragment extends Fragment {
    private static final String TAG = "PersonsFragment";
    private static final int MSG_DB = 0;

    private PersonsHandler handler;

    private AnimeDBHelper dbHelper;

    private RecyclerView recyclerView;
    private PersonsAdapter adapter;

    private List<Person> personList = new ArrayList<>();

    private int filter_job_position = 0;
    private String filter_job = "";
    private int filter_sex_position = 0;
    private String filter_sex = "";

    private String keyword = "";

    private int subjectId = 0;
    private int characterId = 0;

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
        characterId = getArguments().getInt("characterId");

        handler = new PersonsHandler(this);

        freshList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_persons, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_persons);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PersonsAdapter(personList);
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
                Log.d(TAG, "initList");
                personList.clear();
                Person person;

                String where = "1 ";
                if (characterId > 0 && subjectId > 0) {
                    where += "AND (id IN (SELECT personId id FROM Star " +
                            "WHERE (Star.subjectId = " + subjectId +
                            " AND Star.characterId = " + characterId +
                            " )))";
                }
                else if (subjectId > 0) {
                    where += "AND (id IN (SELECT personId id FROM Make " +
                            "WHERE Make.subjectId = " + subjectId +
                            " ))";

                }
                switch (filter_job_position) {
                    case 0:
                        break;
                    case 8:
                        where += "AND (job = '') ";
                        break;
                    default:
                        where += "AND (job LIKE '%" + filter_job + "%') ";
                        break;
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
                    where += "AND ((personName LIKE '%" + keyword + "%') " +
                            "OR (nameCHS LIKE '%" + keyword + "%') " +
                            "OR (alias LIKE '%" + keyword + "%') " +
                            "OR (detail LIKE '%" + keyword + "%') " +
                            "OR (id IN (SELECT personId id FROM Make " +
                            "WHERE (duty LIKE '%" + keyword + "%')))) ";
                }





                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("Person", null, where, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        person = new Person();
                        person.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        person.setPersonName(cursor.getString(cursor.getColumnIndex("personName")));
                        person.setNameCHS(cursor.getString(cursor.getColumnIndex("nameCHS")));
                        person.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                        person.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
                        person.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                        person.setJob(cursor.getString(cursor.getColumnIndex("job")));
                        String duty = "";
                        if (characterId == 0 && subjectId > 0) {
                            Cursor cursor1 = db.query("Make", null, "subjectId = ? AND personId = ?",
                                    new String[]{String.valueOf(subjectId), String.valueOf(person.getId())},
                                    null, null, null);

                            if (cursor1.moveToFirst()) {
                                do {
                                    duty += cursor1.getString(cursor1.getColumnIndex("duty")) + " ";
                                } while (cursor1.moveToNext());
                            }

                            cursor1.close();
                        }
                        person.setDuty(duty);





                        personList.add(person);
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

    public void freshList() {
        Activity activity = getActivity();
        if (activity instanceof MakersActivity) {
            ((MakersActivity) activity).showProgressBar();
        }
        initList();
    }

    private void freshView() {
        Activity activity = getActivity();
        if (activity instanceof MakersActivity) {
            ((MakersActivity) activity).hideProgressBar();
        }
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    public void filter() {
        final View dialogView = View.inflate(getContext(), R.layout.dialog_persons_filter, null);
        final Spinner job = (Spinner) dialogView.findViewById(R.id.persons_filter_job);
        job.setSelection(filter_job_position);
        final Spinner sex = (Spinner) dialogView.findViewById(R.id.persons_filter_sex);
        sex.setSelection(filter_sex_position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setTitle(R.string.filter)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter_job_position = job.getSelectedItemPosition();
                        filter_job = (String) job.getSelectedItem();
                        filter_sex_position = sex.getSelectedItemPosition();
                        filter_sex = (String) sex.getSelectedItem();

                        initList();
                    }
                })
                .show();
    }

    public void showSearchBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_persons_search, null);
        final EditText editText = (EditText) layout.findViewById(R.id.persons_search_keyword);
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

    public static PersonsFragment newInstance(int subjectId, int characterId) {
        PersonsFragment personsFragment = new PersonsFragment();
        Bundle args = new Bundle();
        args.putInt("subjectId", subjectId);
        args.putInt("characterId", characterId);
        personsFragment.setArguments(args);
        return personsFragment;
    }

    private static class PersonsHandler extends Handler {
        private final WeakReference<PersonsFragment> fragmentWeakReference;

        PersonsHandler(PersonsFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    PersonsFragment fragment = fragmentWeakReference.get();
                    fragment.freshView();
                    break;
                default:
            }
        }
    }

}
