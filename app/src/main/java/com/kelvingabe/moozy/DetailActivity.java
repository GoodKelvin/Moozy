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

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView overviewTextView;
    TextView popularTextView;
    TextView releaseDateTextView;
    ImageView moviePosterImageView;
    Button movieTrailerButton;
    String title, releaseDate, popularVote, overview, path, trailer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        title = intent.getStringExtra("title"); releaseDate = intent.getStringExtra("releaseDate");
        popularVote = intent.getStringExtra("popularVote"); overview = intent.getStringExtra("overview");
        path = intent.getStringExtra("path");  trailer = intent.getStringExtra("trailer");
        initializeStuff();
        populateViews();
    }

    public void initializeStuff(){
        overviewTextView = (TextView) findViewById(R.id.movie_detail_overview);
        popularTextView = (TextView) findViewById(R.id.movie_detail_popular_vote);
        releaseDateTextView = (TextView) findViewById(R.id.movie_detail_release_date);
        titleTextView = (TextView) findViewById(R.id.movie_detail_movie_title);
        moviePosterImageView = (ImageView) findViewById(R.id.movie_detail_poster);
        movieTrailerButton = (Button) findViewById(R.id.movie_detail_trailer);
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
                watchYoutubeVideo(trailer);
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

}
