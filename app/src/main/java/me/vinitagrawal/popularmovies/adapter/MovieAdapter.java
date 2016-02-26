package me.vinitagrawal.popularmovies.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.pojo.Result;

/**
 * Created by vinit on 24/2/16.
 */
public class MovieAdapter extends ArrayAdapter<Result> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    public MovieAdapter(Activity context, List<Result> resultList) {
        super(context, 0, resultList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Result movie = getItem(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie,parent,false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.grid_item_movie_poster);

        //using picasso library to download and display images
        Picasso.with(getContext()).load(String.format("%s%s", IMAGE_URL, movie.getPoster_path()))
                .placeholder(R.color.colorAccent)
                .into(posterView);

        return convertView;
    }
}
