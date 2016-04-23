package me.vinitagrawal.popularmovies.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.vinitagrawal.popularmovies.BuildConfig;
import me.vinitagrawal.popularmovies.R;
import me.vinitagrawal.popularmovies.data.MovieContract;
import me.vinitagrawal.popularmovies.data.MovieDbOperations;
import me.vinitagrawal.popularmovies.pojo.Movie;
import me.vinitagrawal.popularmovies.pojo.Review;
import me.vinitagrawal.popularmovies.pojo.ReviewPage;
import me.vinitagrawal.popularmovies.pojo.Trailer;
import me.vinitagrawal.popularmovies.pojo.TrailerResults;
import me.vinitagrawal.popularmovies.service.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DATA_LOADER = 0;
    private static final String SAVED_IMAGE_URL = "/PopularMovies";
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500";
    private static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_THUMBNAIL_IMAGE_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_HQ_IMAGE = "/hqdefault.jpg";
    public static final String DETAIL_MOVIE = "movie";
    public static final String DUAL_PANE = "dualPane";
    private static final String FETCH_TRAILERS = "trailers";
    private static final String FETCH_REVIEWS = "reviews";
    private static final String FETCH_IS_FAVORED = "isFavored";
    private static final String FETCHING_COMPONENT_BUNDLE_KEY = "fetchComponent";
    private static final String MOVIE_SAVEDINSTANCE_KEY = "movie";
    private static final String TRAILERS_SAVEDINSTANCE_KEY = "trailers";
    private static final String REVIEWS_SAVEDINSTANCE_KEY = "reviews";

    private Movie movie;
    private ArrayList<Trailer> trailers = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();
    private String componentType;
    private boolean dualPane;

    private TextView mMovieTitleView;
    private TextView mReleaseDateView;
    private TextView mUserRatingView;
    private TextView mPlotSynopsisView;
    private ImageView mMoviePosterView;
    private ImageView mBackdropImageView;
    private ImageButton mFavoredButton;
    private ViewGroup mMovieReviewsContainer;
    private ViewGroup mMovieTrailersContainer;


    // columns and column positions of review table
    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_AUTHOR_NAME = 1;
    public static final int COL_REVIEW_CONTENT = 2;

    // columns and column positions of trailer table
    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_SITE,
            MovieContract.TrailerEntry.COLUMN_TRAILER_KEY
    };

    public static final int COL_TRAILER_ID = 0;
    public static final int COL_TRAILER_SITE = 1;
    public static final int COL_TRAILER_KEY = 2;

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mMovieTitleView = (TextView) rootView.findViewById(R.id.movie_title);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mUserRatingView = (TextView) rootView.findViewById(R.id.user_rating);
        mPlotSynopsisView = (TextView) rootView.findViewById(R.id.plot_synopsis);
        mMoviePosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        mFavoredButton = (ImageButton) rootView.findViewById(R.id.favorite_movie_button);
        mMovieReviewsContainer = (ViewGroup) rootView.findViewById(R.id.movie_reviews_container);
        mMovieTrailersContainer = (ViewGroup) rootView.findViewById(R.id.movie_trailers_container);

        setHasOptionsMenu(true);
        //reading parcel data from intent, to display the details of the movie selected on the movie list screen
        Bundle bundle = getArguments();
        dualPane = false;
        if(bundle!=null) {
            movie = bundle.getParcelable(DETAIL_MOVIE);
            dualPane = bundle.getBoolean(DUAL_PANE);
        }

        if(!dualPane) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                mBackdropImageView = (ImageView) activity.findViewById(R.id.app_bar_image);
                appBarLayout.setTitle(movie.getOriginal_title());
                appBarLayout.setExpandedTitleColor(Color.TRANSPARENT);
            }
        }
        else {
            mBackdropImageView = (ImageView) rootView.findViewById(R.id.movie_backdrop_image);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if(movie!=null) {
            try {
                //format date into readable format
                mReleaseDateView.setText(getReadableDateString(movie.getRelease_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mMovieTitleView.setText(movie.getOriginal_title());
            mUserRatingView.setText(String.format("Average Rating: %s", String.valueOf(movie.getVote_average())));
            mPlotSynopsisView.setText(movie.getOverview());

            if(!movie.isFavorite()) {
                Picasso.with(getContext()).load(String.format("%s%s", IMAGE_URL, movie.getPoster_path()))
                        .into(mMoviePosterView);

                Picasso.with(getContext()).load(String.format("%s%s", IMAGE_URL, movie.getBackdrop_path()))
                        .placeholder(R.color.colorPrimary)
                        .into(mBackdropImageView);
            }
            else {
                Picasso.with(getContext()).load(new File(getActivity().getExternalFilesDir(null) + SAVED_IMAGE_URL, movie.getPoster_path()))
                        .into(mMoviePosterView);

                Picasso.with(getContext()).load(new File(getActivity().getExternalFilesDir(null) + SAVED_IMAGE_URL, movie.getBackdrop_path()))
                        .placeholder(R.color.colorPrimary)
                        .into(mBackdropImageView);
            }

            mFavoredButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(movie.isFavorite()) {
                        mFavoredButton.setImageResource(R.drawable.ic_favorite_border);
                        movie.setIsFavorite(false);
                        if(dualPane)
                            if(MainActivityFragment.sortType.equals(MainActivityFragment.SORT_BY_FAVORITES))
                                getActivity().getSupportFragmentManager().beginTransaction().remove(MovieDetailsFragment.this).commit();
                        removeMovieFromFavorites();
                    }
                    else {
                        mFavoredButton.setImageResource(R.drawable.ic_favorite_full);
                        movie.setIsFavorite(true);
                        addMovieToFavorites();
                    }
                }
            });

            if(savedInstanceState!=null) {
                if(savedInstanceState.containsKey(MOVIE_SAVEDINSTANCE_KEY))
                    movie = savedInstanceState.getParcelable(MOVIE_SAVEDINSTANCE_KEY);

                if(movie.isFavorite())
                    mFavoredButton.setImageResource(R.drawable.ic_favorite_full);
                else
                    mFavoredButton.setImageResource(R.drawable.ic_favorite_border);

                if (savedInstanceState.containsKey(REVIEWS_SAVEDINSTANCE_KEY)) {
                    reviews = savedInstanceState.getParcelableArrayList(REVIEWS_SAVEDINSTANCE_KEY);
                    onReviewsLoaded();
                } else {
                    if (movie.isFavorite())
                        getLoaderManager().initLoader(DATA_LOADER, setBundleForLoader(FETCH_REVIEWS), this);
                    else
                        fetchReviews();
                }

                if (savedInstanceState.containsKey(TRAILERS_SAVEDINSTANCE_KEY)) {
                    trailers = savedInstanceState.getParcelableArrayList(TRAILERS_SAVEDINSTANCE_KEY);
                    onTrailersLoaded();
                } else {
                    if (movie.isFavorite())
                        getLoaderManager().initLoader(DATA_LOADER, setBundleForLoader(FETCH_TRAILERS), this);
                    else
                        fetchTrailers();
                }
            }
            else {
                getLoaderManager().initLoader(DATA_LOADER, setBundleForLoader(FETCH_IS_FAVORED), this);
            }

        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                shareMovieDetails();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_SAVEDINSTANCE_KEY, movie);
        if(!trailers.isEmpty())
            outState.putParcelableArrayList(TRAILERS_SAVEDINSTANCE_KEY, trailers);
        if(!reviews.isEmpty())
            outState.putParcelableArrayList(REVIEWS_SAVEDINSTANCE_KEY, reviews);
    }

    // method to share movie trailer with other apps
    public void shareMovieDetails() {
        if(!trailers.isEmpty()) {
            String message = "Watch " + movie.getOriginal_title() + " Trailer " + YOUTUBE_VIDEO_URL + trailers.get(0).getKey();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share Via"));
        }
    }

    /**
     * method to set bundle for loader
     * @param fetchComponent the fetching component
     * @return the bundle for loader
     */
    public Bundle setBundleForLoader(String fetchComponent) {
        componentType = fetchComponent;
        Bundle bundle = new Bundle();
        bundle.putString(FETCHING_COMPONENT_BUNDLE_KEY, componentType);
        return bundle;
    }

    // declare base url and return movieApiService
    public MovieApiService getMovieApiService() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-mm-dd")
                .create();

        String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(MovieApiService.class);
    }

    /**
     * method to fetch reviews of a movies
     * asynchronously using retrofit api
     */
    public void fetchReviews() {

        componentType = FETCH_REVIEWS;

        MovieApiService service = getMovieApiService();

        Call<ReviewPage> call = service.getMovieReviews(movie.getId(), BuildConfig.THE_MOVIEDB_API_KEY);

        call.enqueue(new Callback<ReviewPage>() {
            @Override
            public void onResponse(Call<ReviewPage> call, Response<ReviewPage> response) {
                if (response.body() != null) {
                    reviews.clear();
                    reviews.addAll(response.body().getResults());
                    onReviewsLoaded();
                    if(trailers.isEmpty())
                        fetchTrailers();
                }
            }

            @Override
            public void onFailure(Call<ReviewPage> call, Throwable t) {

            }
        });
    }

    /**
     * method to fetch trailers of a movies
     * asynchronously using retrofit api
     */
    public void fetchTrailers() {

        componentType = FETCH_TRAILERS;

        MovieApiService service = getMovieApiService();

        Call<TrailerResults> call = service.getMovieTrailers(movie.getId(), BuildConfig.THE_MOVIEDB_API_KEY);

        call.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Call<TrailerResults> call, Response<TrailerResults> response) {
                if (response.body() != null) {
                    trailers.clear();
                    trailers.addAll(response.body().getResults());
                    onTrailersLoaded();
                }
            }

            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {

            }
        });

    }

    // method to display reviews
    public void onReviewsLoaded() {
        try {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            if (!reviews.isEmpty()) {
                for (Review review : reviews) {

                    View reviewView = inflater.inflate(R.layout.review_item_detail, mMovieReviewsContainer, false);
                    TextView reviewAuthorView = (TextView) reviewView.findViewById(R.id.review_item_author_name);
                    TextView reviewContentView = (TextView) reviewView.findViewById(R.id.review_item_content);

                    reviewAuthorView.setText(review.getAuthor());
                    reviewContentView.setText(review.getContent());

                    mMovieReviewsContainer.addView(reviewView);
                }
                mMovieReviewsContainer.setVisibility(View.VISIBLE);
            } else {
                mMovieReviewsContainer.setVisibility(View.INVISIBLE);
            }

        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // method to display trailers
    public void onTrailersLoaded() {
        try {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            if (!trailers.isEmpty()) {
                for (final Trailer trailer : trailers) {

                    View trailerView = inflater.inflate(R.layout.trailer_item_detail, mMovieTrailersContainer, false);
                    ImageView trailerThumbnailView = (ImageView) trailerView.findViewById(R.id.trailer_thumbnail);
                    ImageView trailerPlayView = (ImageView) trailerView.findViewById(R.id.play_video_image);

                    if(!movie.isFavorite())
                        Picasso.with(getContext()).load(String.format("%s%s%s", YOUTUBE_THUMBNAIL_IMAGE_URL, trailer.getKey(), YOUTUBE_THUMBNAIL_HQ_IMAGE))
                                .into(trailerThumbnailView);
                    else
                        Picasso.with(getContext()).load(new File(getActivity().getExternalFilesDir(null) + SAVED_IMAGE_URL, trailer.getKey() + ".jpg"))
                                .into(trailerThumbnailView);

                    trailerPlayView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(YOUTUBE_VIDEO_URL + trailer.getKey()));
                            startActivity(intent);
                        }
                    });

                    if (trailer.getId().equals(trailers.get(trailers.size() - 1).getId())) {
                        View horizontalRuleView = trailerView.findViewById(R.id.horizontal_rule);
                        horizontalRuleView.setVisibility(View.VISIBLE);
                    }

                    mMovieTrailersContainer.addView(trailerView);
                }
                mMovieTrailersContainer.setVisibility(View.VISIBLE);
            } else {
                mMovieReviewsContainer.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Format date into readable format
     * @param time The time
     * @return readable date in string format
     * @throws ParseException
     */
    private String getReadableDateString(String time) throws ParseException {
        SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        Date date = parseDateFormat.parse(time);

        SimpleDateFormat shortDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return shortDateFormat.format(date);
    }

    // remove favored movie from SQLite database
    public void removeMovieFromFavorites() {
        MovieDbOperations movieDbOperations = new MovieDbOperations(getActivity());
        movieDbOperations.removeFavouriteMovie(movie.getId());

        Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_LONG).show();
    }

    // add favored movies to SQLite database
    public void addMovieToFavorites() {
        MovieDbOperations movieDbOperations = new MovieDbOperations(getActivity());
        movieDbOperations.addFavouriteMovie(movie, trailers, reviews);

        Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (args.getString(FETCHING_COMPONENT_BUNDLE_KEY)) {
            case FETCH_TRAILERS:
                return new CursorLoader(getActivity(), MovieContract.TrailerEntry.CONTENT_URI, TRAILER_COLUMNS,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(movie.getId())},null);
            case FETCH_REVIEWS:
                return new CursorLoader(getActivity(), MovieContract.ReviewEntry.CONTENT_URI, REVIEW_COLUMNS,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(movie.getId())},null);
            case FETCH_IS_FAVORED:
                return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(movie.getId())}, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (componentType) {
            case FETCH_TRAILERS:
                List<Trailer> trailerList = new ArrayList<>();
                if(cursor.moveToFirst())
                    do {
                        trailerList.add(Trailer.fromCursor(cursor));
                    }while (cursor.moveToNext());

                trailers.clear();
                trailers.addAll(trailerList);
                onTrailersLoaded();
                break;
            case FETCH_REVIEWS:
                if(trailers.isEmpty())
                    getLoaderManager().restartLoader(DATA_LOADER, setBundleForLoader(FETCH_TRAILERS), this);

                List<Review> reviewList = new ArrayList<>();
                if(cursor.moveToFirst())
                    do {
                        reviewList.add(Review.fromCursor(cursor));
                    }while (cursor.moveToNext());

                reviews.clear();
                reviews.addAll(reviewList);
                onReviewsLoaded();
                break;
            case FETCH_IS_FAVORED:
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        mFavoredButton.setImageResource(R.drawable.ic_favorite_full);
                        movie.setIsFavorite(true);
                        getLoaderManager().restartLoader(DATA_LOADER, setBundleForLoader(FETCH_REVIEWS), this);
                    }
                    else {
                        mFavoredButton.setImageResource(R.drawable.ic_favorite_border);
                        movie.setIsFavorite(false);
                        fetchReviews();
                    }
                    cursor.close();
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
