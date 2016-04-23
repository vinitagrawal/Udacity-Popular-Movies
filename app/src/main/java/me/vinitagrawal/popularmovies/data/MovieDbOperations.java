package me.vinitagrawal.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.vinitagrawal.popularmovies.pojo.Movie;
import me.vinitagrawal.popularmovies.pojo.Review;
import me.vinitagrawal.popularmovies.pojo.Trailer;

public class MovieDbOperations {

    private Context mContext;
    private static final String SAVED_IMAGE_URL = "/PopularMovies";
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500";
    private static final String YOUTUBE_THUMBNAIL_IMAGE_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_HQ_IMAGE = "/hqdefault.jpg";

    public MovieDbOperations(Context context) {
        mContext = context;
    }

    /**
     * add movie, its trailers and review to database
     * @param movie the movie
     * @param trailers the trailers
     * @param reviews the reviews
     */
    public void addFavouriteMovie(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
        // insert into movies table
        saveImage(IMAGE_URL+movie.getPoster_path(), movie.getPoster_path());
        saveImage(IMAGE_URL+movie.getBackdrop_path(), movie.getBackdrop_path());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginal_title());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        // insert into trailers table
        if(!trailers.isEmpty())
            for(Trailer trailer : trailers) {
                saveImage(YOUTUBE_THUMBNAIL_IMAGE_URL+trailer.getKey()+YOUTUBE_THUMBNAIL_HQ_IMAGE, trailer.getKey()+".jpg");
                contentValues.clear();
                contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie.getId());
                contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
                contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
                contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, trailer.getSite());

                mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
            }

        // insert into reviews table
        if(!reviews.isEmpty())
            for(Review review : reviews) {
                contentValues.clear();

                contentValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.getId());
                contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                contentValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME, review.getAuthor());
                contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());

                mContext.getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, contentValues);
            }
    }

    /**
     * remove movie, its trailers and reviews from favourites
     * @param movie_id the movie id
     */
    public void removeFavouriteMovie(int movie_id) {

        // remove from movies table
        mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(movie_id)});

        // remove from trailers table
        mContext.getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(movie_id)});

        // remove from reviews table
        mContext.getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(movie_id)});
    }

    public void saveImage(String url, final String fileName) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File fileDir = new File(mContext.getExternalFilesDir(null) + SAVED_IMAGE_URL);
                        if(!fileDir.exists())
                            fileDir.mkdir();
                        File file = new File(fileDir,fileName);
                        try {
                            file.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(mContext).load(url).into(target);
    }
}
