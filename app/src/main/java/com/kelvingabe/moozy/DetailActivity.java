package com.kelvingabe.moozy;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kelvingabe.moozy.database.MovieDatabase;
import com.kelvingabe.moozy.database.MovieEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class DetailActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView overviewTextView;
    TextView popularTextView;
    TextView releaseDateTextView, movieReviews;
    ImageView moviePosterImageView;
    Button movieTrailerButton;
    String title, releaseDate, popularVote, overview, path, trailer, movieId;
    String[] trailerIds, authors, contents;
    private ToggleButton toggleButton;
    private MovieDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDb = MovieDatabase.getInstance(getApplicationContext());
        Intent intent = getIntent();
        title = intent.getStringExtra("title"); releaseDate = intent.getStringExtra("releaseDate");
        popularVote = intent.getStringExtra("popularVote"); overview = intent.getStringExtra("overview");
        path = intent.getStringExtra("path");  trailer = intent.getStringExtra("trailer");
        movieId = intent.getStringExtra("movieId");
        if (trailer == null){
            Log.d("DETAILACTIVITY","NO TRAILER INFO");
        }
        try {
            trailerVollley();
            ReviewVollley();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        initializeStuff();
        populateViews();
    }


    public void initializeStuff(){
        movieReviews = findViewById(R.id.movie_detail_reviews);
        overviewTextView = (TextView) findViewById(R.id.movie_detail_overview);
        popularTextView = (TextView) findViewById(R.id.movie_detail_popular_vote);
        releaseDateTextView = (TextView) findViewById(R.id.movie_detail_release_date);
        titleTextView = (TextView) findViewById(R.id.movie_detail_movie_title);
        moviePosterImageView = (ImageView) findViewById(R.id.movie_detail_poster);
        movieTrailerButton = (Button) findViewById(R.id.movie_detail_trailer);
        toggleButton = findViewById(R.id.toggleButton);
    }

    public void populateViews(){
        titleTextView.setText(title); popularTextView.setText(popularVote+"/10");
        releaseDateTextView.setText(releaseDate); overviewTextView.setText(overview);
        Picasso picasso = Picasso.with(this);
        picasso.setIndicatorsEnabled(true);
        picasso.load(path)
                .placeholder(getDrawable(R.mipmap.image_placeholder))
                .error(getDrawable(R.mipmap.image_placeholder))
                .into(moviePosterImageView);
        movieTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(trailerIds[0]);
            }
        });

    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        Log.d("trailer id", id);
        try {
            startActivity(webIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void trailerVollley() throws MalformedURLException {
        String baseUrl1 = "http://api.themoviedb.org/3/movie/";
        String baseUrl2 = "/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(this);

            String url = baseUrl1 + movieId.trim() + baseUrl2;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("Volley", "Response is: " + response);
                            parseTrailerJson(response);
                            //initializeAdapter();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Volley", "That didn't work!");
                    error.printStackTrace();
                }
            });
            queue.add(stringRequest);
    }

    private void parseTrailerJson(String moviesJsonStr) {
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
            trailerIds = new String[j];
            for (int i = 0; i < picPaths.length(); i++) {
                JSONObject c = picPaths.getJSONObject(i);
                String trailer_id = c.getString(TRAILER_KEY);
                trailerIds[i] = trailer_id;
                Log.d(LOG_TAG + " traler", trailer_id);
                Log.d(LOG_TAG + " tralid", trailerIds[i]);
                Log.d(LOG_TAG + " posnum", String.valueOf(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ReviewVollley() throws MalformedURLException {
        String baseUrl1 = "http://api.themoviedb.org/3/movie/";
        String baseUrl2 = "/reviews?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = baseUrl1 + movieId.trim() + baseUrl2;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Review", "Response is: " + response);
                        parseReviewJson(response);
                        //initializeAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Review", "That didn't work!");
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    private void parseReviewJson(String moviesJsonStr) {
        String LOG_TAG = "parseReviewJson";

        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_ID = "id";
        final String REVIEW_URL = "url";
        final String REVIEW_CONTENT = "content";


        try {
            JSONObject reader = new JSONObject(moviesJsonStr);
            //Log.d("parseJson", reader.getString(TMDB_PAGE));
            //JSONObject sys  = reader.getJSONObject("sys");
            //country = sys.getString("country");
            JSONArray picPaths = reader.getJSONArray(REVIEW_RESULTS);
            int j = picPaths.length();
            authors = new String[j];
            contents = new String[j];
            for (int i = 0; i < picPaths.length(); i++) {
                JSONObject c = picPaths.getJSONObject(i);
                String author = c.getString(REVIEW_AUTHOR);
                String content = c.getString(REVIEW_CONTENT);
                authors[i] = author;
                contents[i] = content;
                movieReviews.append(authors[i]+contents[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onFavoriteClicked(View v){
        //check state of button
        //for now just submit in database and view
        //dont submit if in on mode
        MovieEntry movieEntry = new MovieEntry(path,"",overview,releaseDate,"",movieId,title,"",title,popularVote,"",trailer,popularVote);
        mDb.movieDao().insertMovie(movieEntry);
    }
}
