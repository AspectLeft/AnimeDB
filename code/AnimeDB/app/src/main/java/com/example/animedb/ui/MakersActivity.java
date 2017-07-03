package com.example.animedb.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.animedb.R;

public class MakersActivity extends AppCompatActivity {

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makers);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_makers);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.person);
        }
        progressBar = (ProgressBar) findViewById(R.id.makers_progressbar);

        int subjectId = getIntent().getIntExtra("subjectId", 0);
        int characterId = getIntent().getIntExtra("characterId", 0);
        if (characterId > 0 && actionBar != null) {
            actionBar.setTitle(R.string.dubber);
        }

        PersonsFragment personsFragment = PersonsFragment.newInstance(subjectId, characterId);

        getFragmentManager().beginTransaction().add(R.id.makers_frame, personsFragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
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
