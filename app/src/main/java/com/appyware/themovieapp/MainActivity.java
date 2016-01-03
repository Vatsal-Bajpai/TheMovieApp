package com.appyware.themovieapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.ResultsPage;

public class MainActivity extends AppCompatActivity {

    GridAdapter gridAdapterPop, gridAdapterRate;
    private Activity curr_activity;
    private Context curr_context;
    GridView gridView;
    private DrawerLayout mDrawerLayout;
    List<MovieDb> listPop, listRate;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);*/

        curr_activity = this;
        curr_context = this;

        //initialisation
        gridView = (GridView) findViewById(R.id.gridView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        new GetMovies().execute();
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {

            try {
                //calling movies API
                TmdbMovies movies = new TmdbApi(getResources().getString(R.string.API_KEY)).getMovies();
                //popular movie list
                ResultsPage<MovieDb> pagePop = movies.getPopularMovieList("en", 1);
                listPop = pagePop.getResults();
                //voting_average movie list
                ResultsPage<MovieDb> pageRate = movies.getTopRatedMovies("en", 1);
                listRate = pageRate.getResults();

                gridAdapterPop = new GridAdapter(curr_activity, curr_context, GridAdapter.KEY_POPULAR);
                gridAdapterRate = new GridAdapter(curr_activity, curr_context, GridAdapter.KEY_RATE);

                //adding movies jnto adapters
                for (int i = 0; i < listPop.size(); i++)
                    gridAdapterPop.addItem(listPop.get(i));
                for (int i = 0; i < listRate.size(); i++)
                    gridAdapterRate.addItem(listRate.get(i));
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(curr_context, "Unavailable! Check your netwrok connection and Retry!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();
            gridView.setAdapter(gridAdapterPop);
            synchronized (gridAdapterPop) {
                gridAdapterPop.notifyAll();
            }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_sort_pop:
                gridView.setAdapter(gridAdapterPop);
                synchronized (gridAdapterPop) {
                    gridAdapterPop.notifyAll();
                }
                break;
            case R.id.action_sort_rate:
                gridView.setAdapter(gridAdapterRate);
                synchronized (gridAdapterRate) {
                    gridAdapterRate.notifyAll();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
