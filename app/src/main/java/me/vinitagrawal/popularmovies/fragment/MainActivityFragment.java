package me.vinitagrawal.popularmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import me.vinitagrawal.popularmovies.BuildConfig;
import me.vinitagrawal.popularmovies.MovieDetailActivity;
import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.adapter.MovieAdapter;
import me.vinitagrawal.popularmovies.pojo.MoviePage;
import me.vinitagrawal.popularmovies.pojo.Result;
import me.vinitagrawal.popularmovies.service.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private List<Result> movieList = new ArrayList<>();
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_RATING = "vote_average.desc";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting options menu to provide sorting options
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), movieList);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        gridView.setAdapter(movieAdapter);

        //setup a grid item listener event to display movie details in a new activity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                //passing movie object in parcelable format
                intent.putExtra("movie", movieAdapter.getItem(position));
                startActivity(intent);
            }
        });

        //fetching movie list for the first time with popularity as the default sort order
        fetchMovies(SORT_BY_POPULARITY);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortPopular) {
            fetchMovies(SORT_BY_POPULARITY);
            return true;
        }
        else if(id == R.id.sortRating) {
            fetchMovies(SORT_BY_RATING);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method to fetch movie list asynchronously using retrofit api
     * and notify the adapter about the new list
     * @param sortKey
     */
    public void fetchMovies(String sortKey) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-mm-dd")
                .create();

        String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieApiService service = retrofit.create(MovieApiService.class);

        Call<MoviePage> call = service.getMovieList(sortKey,BuildConfig.THE_MOVIEDB_API_KEY);

        call.enqueue(new Callback<MoviePage>() {
            @Override
            public void onResponse(Call<MoviePage> call, Response<MoviePage> response) {
                if(response.body()!=null) {
                    movieList.clear();
                    movieAdapter.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MoviePage> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
