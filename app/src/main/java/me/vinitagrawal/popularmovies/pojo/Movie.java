package me.vinitagrawal.popularmovies.pojo;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import me.vinitagrawal.popularmovies.fragment.MainActivityFragment;

public class Movie implements Parcelable{

    private String poster_path;
    private String overview;
    private String release_date;
    private int id;
    private String original_title;
    private Double vote_average;
    private String backdrop_path;
    private boolean isFavorite;

    public Movie() {
    }

    protected Movie(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        original_title = in.readString();
        vote_average = in.readDouble();
        backdrop_path = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     *
     * @return
     * The poster_path
     */
    public String getPoster_path() {
        return poster_path;
    }

    /**
     *
     * @param poster_path
     * The poster_path
     */
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }


    /**
     *
     * @return
     * The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     *
     * @param overview
     * The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     *
     * @return
     * The release_date
     */
    public String getRelease_date() {
        return release_date;
    }

    /**
     *
     * @param release_date
     * The release_date
     */
    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The original_title
     */
    public String getOriginal_title() {
        return original_title;
    }

    /**
     *
     * @param original_title
     * The original_title
     */
    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    /**
     *
     * @return
     * The vote_average
     */
    public Double getVote_average() {
        return vote_average;
    }

    /**
     *
     * @param vote_average
     * The vote_average
     */
    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "poster_path='" + poster_path + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                ", id=" + id +
                ", original_title='" + original_title + '\'' +
                ", vote_average=" + vote_average +
                ", backdrop_path=" + backdrop_path +
                ", isFavorite=" + isFavorite +
                '}';
    }

    public static Movie fromCursor(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getInt(MainActivityFragment.COL_MOVIE_ID));
        movie.setPoster_path(cursor.getString(MainActivityFragment.COL_POSTER_PATH));
        movie.setBackdrop_path(cursor.getString(MainActivityFragment.COL_BACKDROP_PATH));
        movie.setOriginal_title(cursor.getString(MainActivityFragment.COL_ORIGINAL_TITLE));
        movie.setOverview(cursor.getString(MainActivityFragment.COL_OVERVIEW));
        movie.setRelease_date(cursor.getString(MainActivityFragment.COL_RELEASE_DATE));
        movie.setVote_average(cursor.getDouble(MainActivityFragment.COL_VOTE_AVERAGE));
        return movie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeDouble(vote_average);
        dest.writeString(backdrop_path);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}