package me.vinitagrawal.popularmovies.service;

import me.vinitagrawal.popularmovies.pojo.MoviePage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vinit on 25/2/16.
 *
 * Interface for retrofit api
 * defining request method and values using annotations
 *
 */
public interface MovieApiService {

    @GET("movie")
    Call<MoviePage> getMovieList(@Query("sort_by") String sort, @Query("api_key") String apiKey);
}
