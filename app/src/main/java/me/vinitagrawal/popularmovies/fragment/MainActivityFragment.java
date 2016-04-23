package me.vinitagrawal.popularmovies.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import me.vinitagrawal.popularmovies.BuildConfig;
import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.activity.MainActivity;
import me.vinitagrawal.popularmovies.adapter.MovieAdapter;
import me.vinitagrawal.popularmovies.data.MovieContract;
import me.vinitagrawal.popularmovies.pojo.Movie;
import me.vinitagrawal.popularmovies.pojo.MoviePage;
import me.vinitagrawal.popularmovies.service.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITE_LOADER = 0;
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_RATING = "top_rated";
    public static final String SORT_BY_FAVORITES = "favorites";
    private static final String MOVIE_SAVEDINSTANCE_KEY = "movieArrayList";
    public static String sortType;

    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList = new ArrayList<>();

    // columns to select for projection
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;
    public static final int COL_BACKDROP_PATH = 2;
    public static final int COL_OVERVIEW = 3;
    public static final int COL_RELEASE_DATE = 4;
    public static final int COL_ORIGINAL_TITLE = 5;
    public static final int COL_VOTE_AVERAGE = 6;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_movies);
        mRecyclerView.setHasFixedSize(true);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // check if savedinstancestate exists and then fetch values from it
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_SAVEDINSTANCE_KEY)) {
            movieAdapter = new MovieAdapter(getActivity(), movieArrayList);
            mRecyclerView.setAdapter(movieAdapter);

            //fetching movie list for the first time with popularity as the default sort order
            fetchMovies(SORT_BY_POPULARITY);
        } else {
                movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_SAVEDINSTANCE_KEY);
                movieAdapter = new MovieAdapter(getActivity(), movieArrayList);
                mRecyclerView.setAdapter(movieAdapter);
        }


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sortPopular:
                clearRecyclerView();
                sortType = SORT_BY_POPULARITY;
                fetchMovies(SORT_BY_POPULARITY);
                return true;
            case R.id.sortRating:
                clearRecyclerView();
                sortType = SORT_BY_RATING;
                fetchMovies(SORT_BY_RATING);
                return true;
            case R.id.sortFavourite:
                clearRecyclerView();
                sortType = SORT_BY_FAVORITES;
                //call init loader
                getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_SAVEDINSTANCE_KEY, movieArrayList);
    }

    // method to check if the internet is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // function to clear the recycler view when sort order is changes
    public void clearRecyclerView() {
        movieArrayList.clear();
        movieAdapter.notifyDataSetChanged();
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.MOVIE_DETAIL_FRAGMENT_TAG);
        if(fragment!=null)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
    }

    /**
     * method to fetch movie list asynchronously using retrofit api
     * and notify the adapter about the new list
     * @param sortKey the key to sort with
     */
    public void fetchMovies(String sortKey) {
        if(isNetworkAvailable()) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-mm-dd")
                    .create();

            String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIE_DB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            MovieApiService service = retrofit.create(MovieApiService.class);

            Call<MoviePage> call = service.getMovieList(sortKey, BuildConfig.THE_MOVIEDB_API_KEY);

            call.enqueue(new Callback<MoviePage>() {
                @Override
                public void onResponse(Call<MoviePage> call, Response<MoviePage> response) {
                    if (response.body() != null) {
                        movieArrayList.addAll(response.body().getResults());
                        movieAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MoviePage> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), R.string.network_unavailable_string, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(sortType.equals(SORT_BY_FAVORITES)) {
            movieArrayList.clear();
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = Movie.fromCursor(cursor);
                    movie.setIsFavorite(true);
                    movieArrayList.add(movie);
                } while (cursor.moveToNext());
            }
            movieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
