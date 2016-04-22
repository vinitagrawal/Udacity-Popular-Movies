package me.vinitagrawal.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int MOVIE = 100;
    static final int TRAILER = 101;
    static final int REVIEW = 102;

    static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;

        // add uri's to matcher
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        int match = mUriMatcher.match(uri);
        switch(match) {
            case MOVIE:
                retCursor = movieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILER:
                retCursor = movieDbHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW:
                retCursor = movieDbHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        Uri returnUri;
        long _id = 0;

        switch (match) {
            case MOVIE:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case TRAILER:
                _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case REVIEW:
                _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsDeleted;

        if(selection == null)
            selection = "1";

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if(rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if(rowsUpdated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return 0;
    }
}
