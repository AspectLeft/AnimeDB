package com.example.animedb.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.animedb.R;
import com.example.animedb.presenter.MainPresenter;
import com.example.animedb.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    public MainPresenter mainPresenter;

    private SubjectsFragment subjectsFragment;
    private FavoritesFragment favoritesFragment = null;
    private PersonsFragment personsFragment = null;
    private CharactersFragment charactersFragment = null;
    private TagsFragment tagsFragment = null;

    private int fragmentState = 0;
    public static final int STATE_SUBJECTS = 1;
    public static final int STATE_FAVORITES = 2;
    public static final int STATE_PEOPLE = 3;
    public static final int STATE_CHARACTERS = 4;
    public static final int STATE_TAGS = 5;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mainPresenter = new MainPresenter(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_dark);
        }
        navigationView.setCheckedItem(R.id.nav_menu_subjects);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mainPresenter);

        subjectsFragment = (SubjectsFragment) getFragmentManager().findFragmentById(R.id.fragment_subjects);
        fragmentState = STATE_SUBJECTS;
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        MainActivity.this.invalidateOptionsMenu();
        drawerLayout.closeDrawers();
        fab.show();
        switch (item.getItemId()) {
            case R.id.nav_menu_subjects:
                if (fragmentState != STATE_SUBJECTS) {
                    fragmentState = STATE_SUBJECTS;
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    if (favoritesFragment != null) {
                        transaction.hide(favoritesFragment);
                    }
                    if (personsFragment != null) {
                        transaction.hide(personsFragment);
                    }
                    if (charactersFragment != null) {
                        transaction.hide(charactersFragment);
                    }
                    if (tagsFragment != null) {
                        transaction.hide(tagsFragment);
                    }
                    transaction.show(subjectsFragment)
                            .commit();
                }

                break;
            case R.id.nav_menu_favorites:
            {
                if (fragmentState != STATE_FAVORITES) {
                    fragmentState = STATE_FAVORITES;
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    if (favoritesFragment == null) {
                        favoritesFragment = new FavoritesFragment();
                        transaction.add(R.id.main_frame, favoritesFragment);
                    }
                    else {
                        transaction.show(favoritesFragment);
                    }
                    if (personsFragment != null) {
                        transaction.hide(personsFragment);
                    }
                    if (charactersFragment != null) {
                        transaction.hide(charactersFragment);
                    }
                    if (tagsFragment != null) {
                        transaction.hide(tagsFragment);
                    }
                    transaction.hide(subjectsFragment).commit();

                }
            }

                break;
            case R.id.nav_menu_people:
                if (fragmentState != STATE_PEOPLE) {
                    fragmentState = STATE_PEOPLE;
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    if (personsFragment == null) {
                        personsFragment = PersonsFragment.newInstance(0, 0);
                        transaction.add(R.id.main_frame, personsFragment);
                    }
                    else {
                        transaction.show(personsFragment);
                    }
                    if (favoritesFragment != null) {
                        transaction.hide(favoritesFragment);
                    }
                    if (charactersFragment != null) {
                        transaction.hide(charactersFragment);
                    }
                    if (tagsFragment != null) {
                        transaction.hide(tagsFragment);
                    }
                    transaction.hide(subjectsFragment).commit();
                }
                break;
            case R.id.nav_menu_characters:
                if (fragmentState != STATE_CHARACTERS) {
                    fragmentState = STATE_CHARACTERS;
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    if (charactersFragment == null) {
                        charactersFragment = CharactersFragment.newInstance(0);
                        transaction.add(R.id.main_frame, charactersFragment);
                    }
                    else {
                        transaction.show(charactersFragment);
                    }
                    if (favoritesFragment != null) {
                        transaction.hide(favoritesFragment);
                    }
                    if (personsFragment != null) {
                        transaction.hide(personsFragment);
                    }
                    if (tagsFragment != null) {
                        transaction.hide(tagsFragment);
                    }
                    transaction.hide(subjectsFragment).commit();
                }
                break;
            case R.id.nav_menu_tags:
                if (fragmentState != STATE_TAGS) {
                    fragmentState = STATE_TAGS;
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    if (tagsFragment == null) {
                        tagsFragment = TagsFragment.newInstance(0);
                        transaction.add(R.id.main_frame, tagsFragment);
                    }
                    else {
                        transaction.show(tagsFragment);
                    }
                    if (favoritesFragment != null) {
                        transaction.hide(favoritesFragment);
                    }
                    if (personsFragment != null) {
                        transaction.hide(personsFragment);
                    }
                    if (charactersFragment != null) {
                        transaction.hide(charactersFragment);
                    }
                    transaction.hide(subjectsFragment).commit();
                }


                break;

            default:
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        switch (fragmentState) {
            case STATE_CHARACTERS:
            case STATE_SUBJECTS:
            case STATE_PEOPLE:
            case STATE_FAVORITES:
                menu.findItem(R.id.main_menu_filter).setVisible(true);
                break;
            case STATE_TAGS:
                menu.findItem(R.id.main_menu_filter).setVisible(false);
                break;

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.main_menu_filter:
                switch (fragmentState) {
                    case STATE_SUBJECTS:
                        subjectsFragment.filter();
                        break;
                    case STATE_PEOPLE:
                        personsFragment.filter();
                        break;
                    case STATE_CHARACTERS:
                        charactersFragment.filter();
                        break;
                    case STATE_FAVORITES:
                        favoritesFragment.filter();
                }

                break;
        }
        return true;
    }

    @Override
    public void showSearchBar() {
        switch (fragmentState) {
            case STATE_SUBJECTS:
                subjectsFragment.showSearchBar();
                break;
            case STATE_PEOPLE:
                personsFragment.showSearchBar();
                break;
            case STATE_CHARACTERS:
                charactersFragment.showSearchBar();
                break;
            case STATE_TAGS:
                tagsFragment.showSearchBar();
                break;
        }
    }

    @Override
    public MainPresenter getPresenter() {
        return mainPresenter;
    }

    @Override
    public void changeFab(int dy) {
        if (dy > 0 && fab.getVisibility() == View.VISIBLE)
        {
            fab.hide();
        }
        else if (dy < 0 && fab.getVisibility() != View.VISIBLE)
        {
            fab.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
