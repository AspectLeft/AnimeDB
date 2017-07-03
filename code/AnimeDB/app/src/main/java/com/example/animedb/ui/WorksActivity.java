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

public class WorksActivity extends AppCompatActivity {
    private SubjectsFragment subjectsFragment = null;

    private int personId;
    private int characterId;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_works);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.works);
        }

        personId = getIntent().getIntExtra("personId", 0);
        characterId = getIntent().getIntExtra("characterId", 0);

        subjectsFragment = SubjectsFragment.newInstance(personId, characterId, "");
        getFragmentManager().beginTransaction().add(R.id.works_frame, subjectsFragment)
                .commit();

        progressBar = (ProgressBar) findViewById(R.id.works_progressbar);
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
