package com.kelvingabe.moozy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import java.net.MalformedURLException;

public class FavoriteMoviesActivity extends AppCompatActivity {
    String sortOrder;
    GridView gridview;
    String[] movieImageUrls;
    private MovieDatabase mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        mDb = MovieDatabase.getInstance(getApplicationContext());
    }

    private void readPrefs() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sortOrder = SP.getString(getString(R.string.pref_movieSortType), "1");
        Log.d("prefs", sortOrder);
    }

    private void initializeAdapter() {
        gridview = (GridView) findViewById(R.id.main_activity_gridview);
        gridview.setAdapter(new MainGridviewAdapter(this, mDb.movieDao().loadAllMovies()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //openDetailedView(position);
            }
        });
        gridview.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPrefs();
        loadData();
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
