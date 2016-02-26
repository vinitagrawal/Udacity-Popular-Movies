package me.vinitagrawal.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.vinitagrawal.popularmovies.pojo.Result;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView movieTitleTextView = (TextView) findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) findViewById(R.id.release_date);
        TextView userRatingTextView = (TextView) findViewById(R.id.user_rating);
        TextView plotSynopsisTextView = (TextView) findViewById(R.id.plot_synonsis);
        ImageView moviePosterImageView = (ImageView) findViewById(R.id.movie_poster);

        //reading parcel data from intent, to display the details of the movie selected on the movie list screen
        Result movie = getIntent().getParcelableExtra("movie");

        try {
            //format date into readable format
            releaseDateTextView.setText(getReadableDateString(movie.getRelease_date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        movieTitleTextView.setText(movie.getOriginal_title());
        userRatingTextView.setText(String.format("%s/10", String.valueOf(movie.getVote_average())));
        plotSynopsisTextView.setText(movie.getOverview());
        Picasso.with(this).load(String.format("%s%s", IMAGE_URL, movie.getPoster_path()))
                .placeholder(R.color.colorAccent)
                .into(moviePosterImageView);

    }

    /**
     * Format date into readable format
     * @param time
     * @return
     * @throws ParseException
     */
    private String getReadableDateString(String time) throws ParseException {
        SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        Date date = parseDateFormat.parse(time);

        SimpleDateFormat shortDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return shortDateFormat.format(date);
    }

}
