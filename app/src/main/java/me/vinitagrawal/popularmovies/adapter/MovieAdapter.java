package me.vinitagrawal.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.pojo.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String SAVED_IMAGE_URL = "/PopularMovies";
    private List<Movie> movieList;
    private Context mContext;

    public interface ItemClickCallback {

        void onItemSelected(Movie movie);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView movieCardView;
        ImageView posterView;

        public ViewHolder(View itemView) {
            super(itemView);
            movieCardView = (CardView) itemView.findViewById(R.id.recycler_item_card_view);
            posterView = (ImageView) itemView.findViewById(R.id.recycler_item_movie_poster);
        }
    }

    public MovieAdapter(Activity context, List<Movie> movieList) {
        this.mContext = context;
        this.movieList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_movie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Movie movie = movieList.get(position);

        //using picasso library to download and display images
        if(!movie.isFavorite())
            Picasso.with(mContext).load(String.format("%s%s", IMAGE_URL, movie.getPoster_path()))
                    .placeholder(R.color.colorAccent)
                    .into(holder.posterView);
        else
            Picasso.with(mContext).load(new File(Environment.getExternalStorageDirectory().getPath() + SAVED_IMAGE_URL, movie.getPoster_path()))
                    .placeholder(R.color.colorAccent)
                    .into(holder.posterView);

        holder.movieCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ItemClickCallback) mContext).onItemSelected(movie);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

}
