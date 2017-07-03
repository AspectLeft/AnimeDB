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
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Tag;
import com.example.animedb.presenter.TagsAdapter;
import com.example.animedb.util.SqlUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fang on 2017/6/17.
 *
 */

public class TagsFragment extends Fragment {
    private static final String TAG = "TagsFragment";
    private static final int MSG_DB = 0;

    private TagsHandler handler;

    private AnimeDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private RecyclerView recyclerView;
    private TagsAdapter adapter;

    private List<Tag> tagList = new ArrayList<>();

    private int subjectId = 0;

    private String keyword = "";

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


        subjectId = getArguments().getInt("subjectId");
        handler = new TagsHandler(this);

        freshList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_tags);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TagsAdapter(tagList);
        recyclerView.setAdapter(adapter);
        if (getActivity() instanceof MainActivity) {
            recyclerView.addOnScrollListener(((MainActivity) getActivity()).getPresenter());
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() == tagList.size() - 1) {
                    //Bottom
                    //Log.d(TAG, "load more.");
                    loadMore();

                }
            }
        });
        return view;
    }


    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initData");
                tagList.clear();
                Tag tag;

                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }

                String where = "1 ";
                if (!keyword.equals("")) {
                    where += "AND tagName LIKE '%" + keyword +
                            "%' ";
                }

                if (subjectId > 0) {
                    where += "AND subjectId = " + subjectId;
                }

                db = dbHelper.getReadableDatabase();
                cursor = db.query("Tagged", new String[]{"tagName", "SUM(voteNum)"}, where, null, "tagName", null, "2 DESC, tagName");


                if (cursor.moveToFirst()) {
                    int i = 0;
                    do {
                        tag = new Tag();
                        tag.setName(cursor.getString(cursor.getColumnIndex("tagName")));
                        tag.setVoteNum(cursor.getInt(1));

                        tagList.add(tag);
                        i++;
                    } while (cursor.moveToNext() && i < 50);
                }


                Message message = new Message();
                message.what = MSG_DB;
                handler.sendMessage(message);

            }
        }).start();


    }

    private void loadMore() {
        Tag tag;
        if (!cursor.isAfterLast()) {
            int i = 0;
            do {
                tag = new Tag();
                tag.setName(cursor.getString(cursor.getColumnIndex("tagName")));
                tag.setVoteNum(cursor.getInt(1));

                tagList.add(tag);
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
        initData();
    }

    public static TagsFragment newInstance(int subjectId) {
        TagsFragment fragment = new TagsFragment();
        Bundle args = new Bundle();
        args.putInt("subjectId", subjectId);
        fragment.setArguments(args);
        return fragment;
    }


    private static class TagsHandler extends Handler {
        private final WeakReference<TagsFragment> fragmentWeakReference;

        TagsHandler(TagsFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DB:
                    TagsFragment fragment = fragmentWeakReference.get();
                    fragment.freshView();
                    break;
                default:
            }
        }
    }

    private void freshView() {
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }

    public void showSearchBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_tags_search, null);
        final EditText editText = (EditText) layout.findViewById(R.id.tags_search_keyword);
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

}
