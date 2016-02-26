package me.vinitagrawal.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable{

    private String poster_path;
    private String overview;
    private String release_date;
    private int id;
    private String original_title;
    private Double vote_average;

    protected Result(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        original_title = in.readString();
        vote_average = in.readDouble();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
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

    @Override
    public String toString() {
        return "Result{" +
                "poster_path='" + poster_path + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                ", id=" + id +
                ", original_title='" + original_title + '\'' +
                ", vote_average=" + vote_average +
                '}';
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
    }
}