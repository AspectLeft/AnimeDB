package com.example.animedb.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.animedb.R;

public class SubjectsWithTagActivity extends AppCompatActivity {
    private SubjectsFragment subjectsFragment = null;

    private String tagName;


    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_with_tag);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subjects_with_tag);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.works);
        }

        tagName = getIntent().getStringExtra("tagName");
        subjectsFragment = SubjectsFragment.newInstance(0, 0, tagName);
        getFragmentManager().beginTransaction().add(R.id.works_frame, subjectsFragment)
                .commit();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_works, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.works_menu_filter:
                subjectsFragment.filter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
