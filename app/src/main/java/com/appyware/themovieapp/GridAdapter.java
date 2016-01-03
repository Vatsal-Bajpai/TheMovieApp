package com.appyware.themovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by appyware on 29/11/15.
 */
public class GridAdapter extends BaseAdapter {

    Activity curr_activity;
    Context curr_context;
    private LayoutInflater mLayoutInflater;
    List<MovieDb> list = new ArrayList<MovieDb>();
    public static final String KEY_POPULAR = "popular";
    public static final String KEY_RATE = "rate";
    String KEY;

    public GridAdapter(Activity activity, Context context, String KEY) {
        mLayoutInflater = LayoutInflater.from(context);

        this.curr_activity = activity;
        this.curr_context = context;
        this.KEY = KEY;
        //this.list = new ArrayList<MovieDb>(list);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {

        ImageView moviePoster;
        TextView movieName, movieSortTypeDef;
        LinearLayout linearLayout;

    }

    public void addItem(MovieDb movieDb) {
        list.add(movieDb);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final ViewHolder mVHolder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_home, parent,
                        false);
                mVHolder = new ViewHolder();

                //initialisation
                mVHolder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
                mVHolder.movieName = (TextView) convertView.findViewById(R.id.movie_name);
                mVHolder.movieSortTypeDef = (TextView) convertView.findViewById(R.id.movie_sort_typedef);
                mVHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.textBackground);

                convertView.setTag(mVHolder);
            } else {
                mVHolder = (ViewHolder) convertView.getTag();
            }

            //checking the KEY
            mVHolder.movieName.setText(list.get(position).getTitle());
            if (KEY.equals(KEY_POPULAR))
                mVHolder.movieSortTypeDef.setText("" + precision(list.get(position).getPopularity()));
            else if (KEY.equals(KEY_RATE))
                mVHolder.movieSortTypeDef.setText("" + precision(list.get(position).getVoteAverage()));

            //loading image
            Picasso.with(curr_context).load("http://image.tmdb.org/t/p/w185" + list.get(position).getPosterPath()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mVHolder.moviePoster.setImageBitmap(bitmap);

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {

                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                mVHolder.linearLayout.setBackgroundColor(swatch.getRgb());
                                mVHolder.movieName.setTextColor(swatch.getTitleTextColor());
                                mVHolder.movieSortTypeDef.setTextColor(swatch.getTitleTextColor());
                            }
                        }
                    });
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

            //onClickItemListener
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(curr_activity, DetailsActivity.class);
                    HashMap<String, String> detail = new HashMap<String, String>();
                    detail.clear();
                    detail.put("name", list.get(position).getTitle());
                    detail.put("date", list.get(position).getReleaseDate());
                    detail.put("vote", "" + precision(list.get(position).getVoteAverage()));
                    detail.put("plot", list.get(position).getOverview());
                    detail.put("path", list.get(position).getPosterPath());
                    intent.putExtra("movieDetail", detail);
                    curr_activity.startActivity(intent);
                }
            });

        } catch (Exception e) {
            System.out.println();
        }

        return convertView;
    }

    public String precision(float value) {
        DecimalFormat df = new DecimalFormat("####0.0");
        return df.format(value);
    }

}
