package com.kelvingabe.moozy;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kelvingabe.moozy.adapters.MainGridviewAdapter;
import com.kelvingabe.moozy.database.MovieDatabase;
import com.kelvingabe.moozy.database.MovieEntry;

import java.util.List;

public class FavoriteMoviesActivity extends AppCompatActivity {
    String sortOrder;
    GridView gridview;
    private MovieDatabase mDb;
    List<MovieEntry> list;
    MainViewModel mainViewModel;
    int index = 0;
    static final String SCROLL_POS = "scroll";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        mDb = MovieDatabase.getInstance(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(SCROLL_POS, gridview.getFirstVisiblePosition());
        outPersistentState.putInt(SCROLL_POS, gridview.getFirstVisiblePosition());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        index = savedInstanceState.getInt(SCROLL_POS);
    }

    private void readPrefs() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sortOrder = SP.getString(getString(R.string.pref_movieSortType), "1");
        Log.d("prefs", sortOrder);
    }

    private void initializeAdapter() {
        gridview = findViewById(R.id.main_activity_gridview);
        gridview.setBackgroundColor(Color.parseColor("#ffffff"));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                openDetailedView(position);
            }
        });
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                gridview.setAdapter(new MainGridviewAdapter(FavoriteMoviesActivity.this, movieEntries));
                list = mainViewModel.getMovies().getValue();
            }
        });
    }

    private void openDetailedView(int i) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", list.get(i).getTitle());
        intent.putExtra("releaseDate", list.get(i).getRelease_date());
        intent.putExtra("popularVote", list.get(i).getPopularity());
        intent.putExtra("overview", list.get(i).getOverview());
        intent.putExtra("path", list.get(i).getPoster_path());
        intent.putExtra("movieId", list.get(i).get_id());
        intent.putExtra("trailer", list.get(i).getVideo());
        intent.putExtra("active", true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPrefs();
        loadData();
        if (index != 0) {
            gridview.smoothScrollToPosition(index);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_refresh) {
            loadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (!sortOrder.equals("3")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            initializeAdapter();
        }
    }
}
