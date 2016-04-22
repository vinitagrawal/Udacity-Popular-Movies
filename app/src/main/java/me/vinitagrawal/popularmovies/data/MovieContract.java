package me.vinitagrawal.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defining tables and columns for the movie database
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "me.vinitagrawal.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TABLE_NAME = "favourite_movies";

        // Column with movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Column with the movie poster path
        public static final String COLUMN_POSTER_PATH = "poster_path";
        // Column with backdrop path
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        // Column with plot synopsis
        public static final String COLUMN_OVERVIEW = "overview";
        // Column with movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";
        // Column with movie title
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        // Column with user rating
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String TABLE_NAME = "movie_trailers";

        // Column with movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Column with trailer id
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        // Column with trailer key
        public static final String COLUMN_TRAILER_KEY = "key";
        // Column with trailer site
        public static final String COLUMN_TRAILER_SITE = "site";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String TABLE_NAME = "movie_reviews";

        // Column with movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Column with review id
        public static final String COLUMN_REVIEW_ID = "review_id";
        // Column with author name
        public static final String COLUMN_AUTHOR_NAME = "author";
        // Column with review content
        public static final String COLUMN_REVIEW_CONTENT = "content";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
