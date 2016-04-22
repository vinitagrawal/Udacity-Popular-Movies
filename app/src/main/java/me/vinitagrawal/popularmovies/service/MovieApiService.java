package me.vinitagrawal.popularmovies.service;

import me.vinitagrawal.popularmovies.pojo.MoviePage;
import me.vinitagrawal.popularmovies.pojo.ReviewPage;
import me.vinitagrawal.popularmovies.pojo.TrailerResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by vinit on 25/2/16.
 *
 * Interface for retrofit api
 * defining request method and values using annotations
 *
 */
public interface MovieApiService {

    @GET("movie/{sort_by}")
    Call<MoviePage> getMovieList(@Path("sort_by") String sortKey, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResults> getMovieTrailers(@Path("id") int movie_id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewPage> getMovieReviews(@Path("id") int movie_id, @Query("api_key") String apiKey);
}
