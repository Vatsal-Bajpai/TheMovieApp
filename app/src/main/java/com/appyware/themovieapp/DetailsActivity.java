package com.appyware.themovieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by appyware on 03/01/16.
 */
public class DetailsActivity extends AppCompatActivity {

    ImageView backDrop;
    TextView movieName, movieDate, movieVote, moviePlot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialisation
        backDrop = (ImageView) findViewById(R.id.movie_poster);
        movieName = (TextView) findViewById(R.id.movie_name);
        movieDate = (TextView) findViewById(R.id.movie_date);
        movieVote = (TextView) findViewById(R.id.movie_vote);
        moviePlot = (TextView) findViewById(R.id.movie_plot);

        HashMap<String, String> detail = (HashMap<String, String>) getIntent().getSerializableExtra("movieDetail");

        Picasso.with(getBaseContext()).load("http://image.tmdb.org/t/p/w185" + detail.get("path")).into(backDrop);
        movieName.setText(detail.get("name"));
        movieDate.setText("Released : " + detail.get("date"));
        movieVote.setText("Avg. Rating : " + detail.get("vote") + "/10");
        moviePlot.setText(detail.get("plot"));

    }

}

