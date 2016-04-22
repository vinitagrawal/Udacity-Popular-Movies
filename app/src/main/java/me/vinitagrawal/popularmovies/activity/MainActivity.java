package me.vinitagrawal.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.adapter.MovieAdapter;
import me.vinitagrawal.popularmovies.fragment.MovieDetailsFragment;
import me.vinitagrawal.popularmovies.pojo.Movie;

/**
 * Created by vinit on 24/2/16.
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickCallback {

    private boolean mDualPane;
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "movieDetailsTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.movie_detail_container) != null) {
            mDualPane = true;
        }
        else {
            mDualPane = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        //passing movie object in parcelable format
        Bundle bundle = new Bundle();
        bundle.putParcelable(MovieDetailsFragment.DETAIL_MOVIE, movie);
        bundle.putBoolean(MovieDetailsFragment.DUAL_PANE, mDualPane);

        if(mDualPane) {
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailsFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
