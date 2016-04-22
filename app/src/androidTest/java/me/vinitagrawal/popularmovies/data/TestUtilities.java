package me.vinitagrawal.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by vinit on 16/3/16.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty Cursor Returned: " + error, valueCursor.moveToFirst());
        validateCursorRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCursorRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse("Column " + columnName + " not found." + error, index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value " + valueCursor.getString(index) + " did not match the expected value "
                    + expectedValue + ". " + error, expectedValue, valueCursor.getString(index));
        }
    }

    static ContentValues createMovieContentValues() {
        // Create a new map of values, where column names are keys
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 1);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "temp_path");
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, " brief description");
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-03-16");
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Deadpool");
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "8.0");

        return contentValues;
    }
}
