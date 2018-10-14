package com.kelvingabe.moozy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kelvingabe.moozy.adapters.MainGridviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String sortOrder;
    String[] eatFoodyImages;
    String[] averageVotes;
    String[] movieTitles;
    String[] releaseDates;
    String[] overviews;
    String[] movieIds;
    String[] trailerIds = new String[100];
    GridView gridview;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        Log.e("test", "test");
    }

    private void loadData() {
        if (isOnline()) {
            try {
                testVollley();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void readPrefs() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sortOrder = SP.getString(getString(R.string.pref_movieSortType), "1");
        Log.d("prefs", sortOrder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPrefs();
        loadData();
    }

    private void initializeAdapter() {
        gridview = (GridView) findViewById(R.id.main_activity_gridview);
        gridview.setAdapter(new MainGridviewAdapter(this, eatFoodyImages));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                openDetailedView(position);
            }
        });
        gridview.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void openDetailedView(int i) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", movieTitles[i]);
        intent.putExtra("releaseDate", releaseDates[i]);
        intent.putExtra("popularVote", averageVotes[i]);
        intent.putExtra("overview", overviews[i]);
        intent.putExtra("path", eatFoodyImages[i]);
        Log.d("Detail posnum", String.valueOf(i));
        intent.putExtra("trailer", trailerIds[i]);
        Log.d("Main Activity","captured trailer for tranfer "+ trailerIds[i]);
        startActivity(intent);
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


    private void parseJson(String moviesJsonStr) {
        String LOG_TAG = "parseJson";
        final String TMDB_PAGE = "page";
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_ADULT = "adult";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_GENRE_IDS = "genre_ids";
        final String TMDB_ID = "id";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_ORIGINAL_LANGUAGE = "original_language";
        final String TMDB_TITLE = "title";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VIDEO = "video";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        try {
            JSONObject reader = new JSONObject(moviesJsonStr);
            Log.d("parseJson", reader.getString(TMDB_PAGE));
            //JSONObject sys  = reader.getJSONObject("sys");
            //country = sys.getString("country");
            JSONArray picPaths = reader.getJSONArray(TMDB_RESULTS);
            int j = picPaths.length();
            eatFoodyImages = new String[j];
            movieTitles = new String[j];
            averageVotes = new String[j];
            overviews = new String[j];
            releaseDates = new String[j];
            movieIds = new String[j];
            for (int i = 0; i < picPaths.length(); i++) {
                JSONObject c = picPaths.getJSONObject(i);
                String path = c.getString(TMDB_POSTER_PATH);
                String overview = c.getString(TMDB_OVERVIEW);
                String title = c.getString(TMDB_TITLE);
                String release_date = c.getString(TMDB_RELEASE_DATE);
                String average_vote = c.getString(TMDB_VOTE_AVERAGE);
                String movie_id = c.getString(TMDB_ID);
                eatFoodyImages[i] = "http://image.tmdb.org/t/p/w185//" + path;
                movieTitles[i] = title;
                releaseDates[i] = release_date;
                overviews[i] = overview;
                averageVotes[i] = average_vote;
                movieIds[i] = movie_id;
                Log.d(LOG_TAG, path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            trailerVollley();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void testVollley() throws MalformedURLException {
        String baseUrl;
        if (sortOrder.equals("1")) {
            baseUrl = "http://api.themoviedb.org/3/movie/popular";
        } else {
            baseUrl = "http://api.themoviedb.org/3/movie/top_rated";
        }
        //  /movie/top_rated
        String apiKey = "?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        URL Url = new URL(baseUrl.concat(apiKey));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url.toString();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Volley", "Response is: " + response);
                        parseJson(response);
                        initializeAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "That didn't work!");
                error.printStackTrace();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void trailerVollley() throws MalformedURLException {
        String baseUrl1 = "http://api.themoviedb.org/3/movie/";
        String baseUrl2 = "/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(this);
        for (i = 0; i < movieIds.length; i++) {
            String url = baseUrl1 + movieIds[i].trim() + baseUrl2;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("Volley", "Response is: " + response);
                            parseTrailerJson(response, i);
                            //initializeAdapter();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Volley", "That didn't work!");
                    error.printStackTrace();
                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        // Request a string response from the provided URL.

    }

    //put first id into array
    private void parseTrailerJson(String moviesJsonStr, int I) {
        String LOG_TAG = "parseTrailerJson";

        final String TRAILER_RESULTS = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";
        final String TRAILER_ISO_639_1 = "iso_639_1";
        final String TRAILER_ID = "id";
        String TRAILER_ISO_3166_1 = "iso_3166_1";

        try {
            JSONObject reader = new JSONObject(moviesJsonStr);
            //Log.d("parseJson", reader.getString(TMDB_PAGE));
            //JSONObject sys  = reader.getJSONObject("sys");
            //country = sys.getString("country");
            JSONArray picPaths = reader.getJSONArray(TRAILER_RESULTS);
            int j = picPaths.length();
            for (int i = 0; i < picPaths.length(); i++) {
                JSONObject c = picPaths.getJSONObject(i);
                String trailer_id = c.getString(TRAILER_KEY);
                trailerIds[i] = trailer_id;
                Log.d(LOG_TAG+" traler", trailer_id);
                Log.d(LOG_TAG+" tralid", trailerIds[i]);
                Log.d(LOG_TAG+" posnum", String.valueOf(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //pass array to delatil

}

