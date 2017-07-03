package com.example.animedb.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.animedb.R;

public class TagsOfSubjectActivity extends AppCompatActivity {
    TagsFragment tagsFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_of_subject);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tags_of_subject);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.tag);
        }

        int subjectId = getIntent().getIntExtra("subjectId", 0);

        tagsFragment = TagsFragment.newInstance(subjectId);

        getFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment)
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

}
