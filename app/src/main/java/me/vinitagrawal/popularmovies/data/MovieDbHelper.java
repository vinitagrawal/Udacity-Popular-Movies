package me.vinitagrawal.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.vinitagrawal.popularmovies.data.MovieContract.MovieEntry;
import me.vinitagrawal.popularmovies.data.MovieContract.TrailerEntry;
import me.vinitagrawal.popularmovies.data.MovieContract.ReviewEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If database schema is changed, the database version must be incremented
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
                " );";

        final String CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL " +
                " );";

        final String CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL " +
                " );";

        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_TRAILER_TABLE);
        db.execSQL(CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
