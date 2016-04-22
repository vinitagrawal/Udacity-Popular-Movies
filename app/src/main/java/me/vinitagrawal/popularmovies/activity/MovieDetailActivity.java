package me.vinitagrawal.popularmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.fragment.MovieDetailsFragment;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();

        if(savedInstanceState==null) {
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, movieDetailsFragment)
                    .commit();
        }
    }

}
