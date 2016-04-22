package me.vinitagrawal.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by vinit on 16/3/16.
 */
public class TestDb extends AndroidTestCase {

    // Since we want each test to start with a clean slate
    void deleteDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function is called before each test is executed, so that we have a clean test.
     */
    public void setUp() {
        deleteDatabase();
    }

    /*
        Test to check if the database was created properly
     */
    public void testCreateDb() throws Throwable {

        // Create a hashSet of required tables
        HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteDatabase();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // get the names of the tables created
        Cursor c = db.rawQuery("Select name from sqlite_master WHERE type='table'", null);
        assertTrue("Database was not created", c.moveToFirst());

        // verify if the tables were created successfully
        tableNameHashSet.remove(c.getString(0));
        assertTrue("Database was created without movie entry table", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",null);
        assertTrue("Unable to query database to get table information", c.moveToFirst());

        // create hashSet of all the columns we want to look for
        HashSet<String> moviesColumnHashSet = new HashSet<>();
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        moviesColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            moviesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Database doesn't contain all the required columns", moviesColumnHashSet.isEmpty());

        c.close();
        db.close();
    }

    public void testFavouriteMoviesTable() throws Throwable {
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();

        // create content values of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieContentValues();

        // insert content values in the database and get a row ID back
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        assertTrue(" Values were inserted", movieRowId != -1);

        // Query the database and receive a cursor back
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);

        // verify if the records were returned
        assertTrue("No records returned",c.moveToFirst());

        // validate data in resulting cursor with the original ContentValues
        TestUtilities.validateCursorRecord("Movie Query Validation Failed", c, contentValues);

        assertFalse("More than one record is returned from the query", c.moveToNext());

        c.close();
        db.close();

    }

}
