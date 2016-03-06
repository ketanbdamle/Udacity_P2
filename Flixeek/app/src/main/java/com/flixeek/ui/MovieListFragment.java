package com.flixeek.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flixeek.R;
import com.flixeek.common.FlixeekConstants;
import com.flixeek.common.SortPrefEnum;
import com.flixeek.contentapi.ContentApi;
import com.flixeek.contentapi.ContentApiHandlerFactory;
import com.flixeek.contentapi.model.MovieDetails;
import com.flixeek.data.MovieDetailsContract;
import com.flixeek.utils.DisplayUtils;
import com.flixeek.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Ketan Damle
 * @version 1.0
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String MOVIE_LISTING = MovieListFragment.class.getName();

    @Bind(R.id.recyclerViewParent)
    View recyclerViewParent;

    @Bind(R.id.movie_list)
    RecyclerView recyclerView;

    @Bind(R.id.progressBarFrame)
    View progressBarFrame;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.sortTitle)
    TextView sortTitle;

    private AlertDialog alertDialog;

    private MovieItemRecyclerViewAdapter movieItemRecyclerViewAdapter;

    private String currOrientation;
    private String currSortPreference;
    private String sortTitleText;
    private List<MovieDetails> movieDetailsList = new ArrayList<>();
    private List<MovieDetails> favoriteMovieDetailsList = new ArrayList<>();
    private List<MovieDetails> storedMovieDetailsList = new ArrayList<>();
    private MovieDetails firstMovieDetails;
    private int lastSelectedMoviePosition;

    private String tmpSortTitleText = "UNDEFINED";
    private boolean isOrientationChanged = false;

    private boolean isContentApiFetchInProgress = false;

    private boolean mTwoPane;

    private static final int STORED_MOVIES_LOADER = 1;

    private static final String[] MOVIE_DETAILS_PROJECTION = new String[]{
            MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_TITLE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_TAGLINE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_OVERVIEW,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_RUNTIME,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_YEAR_OF_RELEASE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_RELEASE_DATE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_GENRES,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_THUMBNAIL,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_FANART,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_CERTIFICATE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_POPULARITY,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_RATING,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_VOTES,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_HOMEPAGE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_WATCHERS,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_MISC_INFO,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_IS_FAVORITE,
            MovieDetailsContract.MovieDetailsEntry.COLUMN_DATE_OF_CREATION
    };

    // these indices must match the projection
    private static final int INDEX_COLUMN_MOVIE_ID = 1;
    private static final int INDEX_COLUMN_TITLE = 2;
    private static final int INDEX_COLUMN_TAGLINE = 3;
    private static final int INDEX_COLUMN_OVERVIEW = 4;
    private static final int INDEX_COLUMN_RUNTIME = 5;
    private static final int INDEX_COLUMN_YEAR_OF_RELEASE = 6;
    private static final int INDEX_COLUMN_RELEASE_DATE = 7;
    private static final int INDEX_COLUMN_GENRES = 8;
    private static final int INDEX_COLUMN_THUMBNAIL = 9;
    private static final int INDEX_COLUMN_FANART = 10;
    private static final int INDEX_COLUMN_MOVIE_CERTIFICATE = 11;
    private static final int INDEX_COLUMN_POPULARITY = 12;
    private static final int INDEX_COLUMN_RATING = 13;
    private static final int INDEX_COLUMN_VOTES = 14;
    private static final int INDEX_COLUMN_HOMEPAGE = 15;
    private static final int INDEX_COLUMN_WATCHERS = 16;
    private static final int INDEX_COLUMN_MISC_INFO = 17;
    private static final int INDEX_COLUMN_IS_FAVORITE = 18;
    private static final int INDEX_COLUMN_DATE_OF_CREATION = 19;

    /**
     * A callback interface that all activities containing this fragment must
     * implement.
     */
    public interface Callback {

        /**
         * Initiates the display of the details of the requested movie.
         *
         * @param movieDetails Details of the Movie requested.
         */
        void onMovieSelected(MovieDetails movieDetails);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MOVIE_LISTING, "Inside onCreate ... ");

        if(savedInstanceState == null || !savedInstanceState.containsKey(FlixeekConstants.BUNDLE_KEY_MOVIE_LIST_DATA)) {
            movieDetailsList = new ArrayList<>();
        }
        else {
            currOrientation= savedInstanceState.getString(FlixeekConstants.BUNDLE_KEY_ORIENTATION);
            currSortPreference = savedInstanceState.getString(FlixeekConstants.BUNDLE_KEY_CURR_SORT_PREF);
            sortTitleText = savedInstanceState.getString(FlixeekConstants.BUNDLE_KEY_SORT_TITLE);
            movieDetailsList= savedInstanceState.getParcelableArrayList(FlixeekConstants.BUNDLE_KEY_MOVIE_LIST_DATA);
            favoriteMovieDetailsList= savedInstanceState.getParcelableArrayList(FlixeekConstants.BUNDLE_KEY_FAVORITE_MOVIE_DETAILS);
            storedMovieDetailsList = savedInstanceState.getParcelableArrayList(FlixeekConstants.BUNDLE_KEY_STORED_MOVIE_DETAILS);
            firstMovieDetails= savedInstanceState.getParcelable(FlixeekConstants.BUNDLE_KEY_FIRST_MOVIE_DETAILS);
            lastSelectedMoviePosition = savedInstanceState.getInt(FlixeekConstants.BUNDLE_KEY_LAST_SELECTED_MOVIE_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.i(MOVIE_LISTING, "Inside onCreateView ... ");

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(MOVIE_LISTING, "Inside onActivityCreated ... ");
        Log.i(MOVIE_LISTING, "Inside onActivityCreated ... getActivity().findViewById(R.id.movie_detail_container) - "+getActivity().findViewById(R.id.movie_detail_container));

        if (getActivity().findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }

        if(sortTitleText!=null) {
            sortTitle.setText(sortTitleText);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortSharedPreference =  prefs.getString(getString(R.string.sort_pref_key), getString(R.string.sort_pref_default));
        if(currSortPreference==null)
            currSortPreference = sortSharedPreference;

        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        String tmpOrientation = (DisplayUtils.isLandscape(getActivity())) ? "LANDSCAPE" : "PORTRAIT";
        if(currOrientation == null)
            currOrientation = tmpOrientation;

        Log.i(MOVIE_LISTING, "Before currOrientation : " + currOrientation);
        if(!tmpOrientation.equalsIgnoreCase(currOrientation)){
            currOrientation = tmpOrientation;
            isOrientationChanged = true;
            Log.i(MOVIE_LISTING, "After currOrientation : "+tmpOrientation);
        }
    }

    /**
     * Configures the recycler view and sets the padding on recycler view parent to center the recycler view.
     *
     * @param recyclerView Recycler View that serves as the container for the movies sourced from Content Api
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        Log.d(MOVIE_LISTING, "mTwoPane: " + mTwoPane);

        recyclerView.setPadding(12, 8, 0, 0);

        int screenWidth;
        int defaultImgPixelWidth = DisplayUtils.getPixelValue(getActivity(), FlixeekConstants.DEFAULT_GRID_IMG_WIDTH);
        Map<String, Integer> gridParams;
        if(!mTwoPane) {
            screenWidth = DisplayUtils.getScreenWidth(getActivity().getWindowManager());
            gridParams = DisplayUtils.computeGridColCountAndMargin(screenWidth, defaultImgPixelWidth, DisplayUtils.isLandscape(getActivity()));
        }
        else{
            screenWidth = (int) (getResources().getDimension(R.dimen.item_width)/getResources().getDisplayMetrics().density);
            gridParams = DisplayUtils.computeGridColCountAndMargin(screenWidth, defaultImgPixelWidth, false);
        }
        int gridColCount = gridParams.get(FlixeekConstants.GRID_COL_COUNT);
        int gridMargin = gridParams.get(FlixeekConstants.GRID_MARGIN);

        Log.d(MOVIE_LISTING, "screenWidth: " + screenWidth);
        Log.d(MOVIE_LISTING, "defaultImgPixelWidth: " + defaultImgPixelWidth);
        Log.d(MOVIE_LISTING, "gridColCount: " + gridColCount);
        Log.d(MOVIE_LISTING, "gridMargin: " + gridMargin);

        recyclerViewParent.setPadding(gridMargin, 0, gridMargin, 0);

        if(getString(R.string.show_favorites_title).equalsIgnoreCase(sortTitleText)) {
            movieItemRecyclerViewAdapter = new MovieItemRecyclerViewAdapter(this, favoriteMovieDetailsList, currSortPreference, mTwoPane, (Callback)getActivity());
        }
        else{
            movieItemRecyclerViewAdapter = new MovieItemRecyclerViewAdapter(this, movieDetailsList, currSortPreference, mTwoPane, (Callback)getActivity());
        }

        recyclerView.setAdapter(movieItemRecyclerViewAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridColCount));
    }

    /**
     * Fetches the movie listing every time after the activity is created or is restarted.
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(MOVIE_LISTING, "Inside onStart ... ");
        if (movieDetailsList == null || movieDetailsList.isEmpty()) {
            Log.d(MOVIE_LISTING, "Invoking getMovies ..");
            getMovies(currSortPreference);
        }
        else if(!getString(R.string.show_favorites_title).equals(sortTitleText)){
            Log.i(MOVIE_LISTING, "Inside onStart - fetching movie details at position: "+lastSelectedMoviePosition);
            getFullMovieDetails(movieDetailsList.get(lastSelectedMoviePosition));
        }
    }

    /* Credit: https://code.google.com/p/android/issues/detail?id=63179 */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(MOVIE_LISTING, "Inside onResume ... ");
        getLoaderManager().initLoader(STORED_MOVIES_LOADER, null, this);
    }

    /**
     * Initiates the movie listing display based on the passed sorting preference
     * @param sortPref Sorting Preference
     */
    public void onSorfPrefChanged(SortPrefEnum sortPref){
        Log.i(MOVIE_LISTING, "Inside onSortPrefChanged ... " + sortPref.name());
        switch (sortPref) {
            case popularity:
                tmpSortTitleText = "UNDEFINED";
                setLastSelectedMoviePosition(0);
                currSortPreference = SortPrefEnum.popularity.name();
                getMovies(currSortPreference);
                break;
            case vote_average:
                tmpSortTitleText = "UNDEFINED";
                setLastSelectedMoviePosition(0);
                currSortPreference = SortPrefEnum.vote_average.name();
                getMovies(currSortPreference);
                break;
            case favorites:
                tmpSortTitleText = "UNDEFINED";
                setLastSelectedMoviePosition(0);
                tmpSortTitleText = getString(R.string.show_favorites_title);
                showFavoriteMovies();
                break;
            default:
                tmpSortTitleText = "UNDEFINED";
                setLastSelectedMoviePosition(0);
                break;
        }
    }

    /**
     * Fetches the movie listing based on given sort preference and network availability via an Async Task {@link com.flixeek.ui.MovieListFragment.MovieDetailsRetriever} depending on the Content Api chosen.
     *
     * @param sortPreference Sort Preference for the movies.
     */
    private void getMovies(String sortPreference) {
        Log.i(MOVIE_LISTING, "Inside getMovies ... ");
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            Log.d(MOVIE_LISTING, "Inside isNetworkAvailable");
            if(!isContentApiFetchInProgress) {
                new MovieDetailsRetriever().execute(sortPreference);
            }
        } else {
            getLoaderManager().restartLoader(STORED_MOVIES_LOADER, null, this);
        }
    }

    /**
     * Initiates and loads all the movie details in addition to the basic details by making the API call.
     * @param movieDetails Basic Movie Details
     */
    void getFullMovieDetails(MovieDetails movieDetails){
        Log.i(MOVIE_LISTING, "Inside getFullMovieDetails ... ");
        new FullMovieDetailsRetriever().execute(movieDetails);
    }

    /**
     * Called when the Content Api call returns a response, hides the progress bar and alert dialog, if any, and shows the movie listing using the Recycler View
     *
     * @param movieDetailsList List of {@link MovieDetails}
     */
    public void updateDisplay(List<MovieDetails> movieDetailsList) {
        Log.d(MOVIE_LISTING, "Inside updateDisplay ..... ");
        if (!movieDetailsList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            progressBarFrame.setVisibility(View.GONE);
            sortTitle.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (alertDialog != null) {
                Log.d(MOVIE_LISTING, "Dismissing alert dialog ...");
                alertDialog.dismiss();
            }
            if(currSortPreference.equals(SortPrefEnum.popularity.name())){
                sortTitleText = getString(R.string.sort_by_popularity_title);
            }
            else if(currSortPreference.equals(SortPrefEnum.vote_average.name())){
                sortTitleText = getString(R.string.sort_by_voter_average_title);
            }
            sortTitle.setText(sortTitleText);
            movieItemRecyclerViewAdapter.setCurrSortPreference(currSortPreference);
            movieItemRecyclerViewAdapter.setMovieDetailsList(movieDetailsList);
            movieItemRecyclerViewAdapter.notifyDataSetChanged();
            getFullMovieDetails(movieDetailsList.get(getLastSelectedMoviePosition()));
        }
        else {
            createAlertDialog(getString(R.string.flixeek_contentapi_failure_title), getString(R.string.flixeek_contentapi_failure_msg), false);
        }
    }

    /**
     * Force loads the stored movie details to fetch the latest favorites added.
     */
    private void showFavoriteMovies() {
        Log.i(MOVIE_LISTING, "Inside showFavoriteMovies ... " + sortTitleText);
        getLoaderManager().getLoader(STORED_MOVIES_LOADER).forceLoad();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(MOVIE_LISTING, "Inside onSaveInstanceState ... ");
        if(getString(R.string.show_favorites_title).equals(sortTitle.getText())){
            if(!favoriteMovieDetailsList.isEmpty() && firstMovieDetails!=null){
                favoriteMovieDetailsList.set(0, firstMovieDetails);
            }
        }
        else{
            Log.i(MOVIE_LISTING, "Inside onSaveInstanceState ... else .....");
            if(!movieDetailsList.isEmpty() && firstMovieDetails!=null) {
                movieDetailsList.set(0, firstMovieDetails);
            }
        }

        outState.putParcelableArrayList(FlixeekConstants.BUNDLE_KEY_MOVIE_LIST_DATA, (ArrayList<? extends Parcelable>) movieDetailsList);
        outState.putString(FlixeekConstants.BUNDLE_KEY_CURR_SORT_PREF, currSortPreference);
        outState.putString(FlixeekConstants.BUNDLE_KEY_SORT_TITLE, sortTitleText);
        outState.putParcelableArrayList(FlixeekConstants.BUNDLE_KEY_FAVORITE_MOVIE_DETAILS, (ArrayList<? extends Parcelable>) favoriteMovieDetailsList);
        outState.putParcelable(FlixeekConstants.BUNDLE_KEY_FIRST_MOVIE_DETAILS, firstMovieDetails);
        outState.putString(FlixeekConstants.BUNDLE_KEY_ORIENTATION, currOrientation);
        outState.putParcelableArrayList(FlixeekConstants.BUNDLE_KEY_STORED_MOVIE_DETAILS, (ArrayList<? extends Parcelable>) storedMovieDetailsList);
        outState.putInt(FlixeekConstants.BUNDLE_KEY_LAST_SELECTED_MOVIE_POSITION, lastSelectedMoviePosition);
    }

    /**
     * Alert Dialog shown to the user in case of Content API failure or network issues.
     *
     * @param title Title of Alert Dialog
     * @param alertContent Content of the Alert Dialog
     * @param isNetworkFailure Has there been a Network Failure
     */
    private void createAlertDialog(String title, String alertContent, boolean isNetworkFailure) {
        Log.d(MOVIE_LISTING, "Inside createDialog ...");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title).setMessage(alertContent);


        builder.setPositiveButton(getString(R.string.nw_fail_retry), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getMovies(currSortPreference);
            }
        });
        if (isNetworkFailure) {
            builder.setNeutralButton(getString(R.string.nw_fail_netsettings), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent networkSettings = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(networkSettings);
                }
            });
        }
        builder.setNegativeButton(getString(R.string.nw_fail_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
                getActivity().finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(MOVIE_LISTING, "Inside onCreateLoader ... ");
        return new CursorLoader(getActivity(),
                MovieDetailsContract.MovieDetailsEntry.CONTENT_URI,
                MOVIE_DETAILS_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Loads the stored and favorite movie details to respective data collections, and updates the display if sorting preference is Favorites.
     * @param loader Stored Movie Details loader.
     * @param data Cursor containing the Stored Movie details from the database.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(MOVIE_LISTING, "Inside onLoadFinished ... ");
        if(STORED_MOVIES_LOADER == loader.getId()) {
            addToDataLists(data);
            Log.d(MOVIE_LISTING, "favoriteMovieDetailsList: " + favoriteMovieDetailsList.size());
            Log.d(MOVIE_LISTING, "sortTitleText: " + sortTitleText);
            Log.d(MOVIE_LISTING, "tmpSortTitleText: " + tmpSortTitleText);
            Log.d(MOVIE_LISTING, "isOrientationChanged: " + isOrientationChanged);
            if (!NetworkUtils.isNetworkAvailable(getActivity()) || getString(R.string.show_favorites_title).equals(tmpSortTitleText) || getString(R.string.show_favorites_title).equals(sortTitleText)) {
                if (favoriteMovieDetailsList != null && favoriteMovieDetailsList.size() > 0) {
                    if(!isOrientationChanged && getString(R.string.show_favorites_title).equals(tmpSortTitleText)){
                        setLastSelectedMoviePosition(0);
                    }
                    Log.d(MOVIE_LISTING, "calling updateDisplay for favorites: " + favoriteMovieDetailsList.get(getLastSelectedMoviePosition()).getTitle());
                    updateDisplay(favoriteMovieDetailsList);
                    sortTitleText = getString(R.string.show_favorites_title);
                    sortTitle.setText(sortTitleText);
                    if(!NetworkUtils.isNetworkAvailable(getActivity())){
                        Toast.makeText(getActivity(), getString(R.string.no_network_show_favorites), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    if (!NetworkUtils.isNetworkAvailable(getActivity())){
                        createAlertDialog(getString(R.string.alertdlg_title_no_network), getString(R.string.alertdlg_content_no_network), true);
                        Toast.makeText(getActivity(), getString(R.string.alertdlg_title_no_network), Toast.LENGTH_SHORT).show();
                    }
                    else if(!isOrientationChanged) {
                        if(getString(R.string.show_favorites_title).equals(tmpSortTitleText)) {
                            Snackbar.make(getActivity().findViewById(R.id.listing_app_bar), "No Favorites added, go ahead and add some, favorites are available even offline.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                        //If no favorites, then load the movie list based on the current set sorting preference.
                        if (movieDetailsList == null || movieDetailsList.isEmpty()) {
                            Log.d(MOVIE_LISTING, "calling updateDisplay for else: ");
                            getMovies(currSortPreference);
                        } else {
                            Log.d(MOVIE_LISTING, "calling updateDisplay for movieDetailsList: " + movieDetailsList.get(0).getTitle());
                            if(getString(R.string.show_favorites_title).equals(sortTitleText))
                                updateDisplay(movieDetailsList);
                        }
                    }
                }
            }

            if(isOrientationChanged) {
                isOrientationChanged = false;
            }
        }
    }

    /**
     * Adds the movie details to the stored and favorites movie collections.
     * @param data Cursor containing the stored movie details
     */
    private void addToDataLists(Cursor data) {
        Log.i(MOVIE_LISTING, "Inside addToDataLists ... ");
        boolean isEmptyResultSet = false;
        while(data.moveToNext()){
            if(!isEmptyResultSet){
                favoriteMovieDetailsList.clear();
                storedMovieDetailsList.clear();
                isEmptyResultSet = true;
            }
            //The second API call though made only for trailer and review details, returns the entire movie details
            // the json info represents the full movie information and hence is simply parsed
            String jsonInfo = data.getString(INDEX_COLUMN_MISC_INFO);
            if(jsonInfo!=null) {
                MovieDetails storedMovieDetails = ContentApiHandlerFactory.handler(ContentApi.valueOf(getString(R.string.default_api))).getMovieDetailsFromJson(jsonInfo);

                //Resets the thumbnail and fanart/backdrop urls to the local cached file paths, because jsoninfo contains raw web links for the same.
                storedMovieDetails.setListingThumbnailUrl(data.getString(INDEX_COLUMN_THUMBNAIL));
                storedMovieDetails.setFanartUrl(data.getString(INDEX_COLUMN_FANART));

                //These columns are in addition to the movie details represented by jsoninfo
                //Date of creation may be used to delete very old cached/stored data in future.
                storedMovieDetails.setFavorite(data.getInt(INDEX_COLUMN_IS_FAVORITE));
                storedMovieDetails.setCreationDate(data.getString(INDEX_COLUMN_DATE_OF_CREATION));
                storedMovieDetailsList.add(storedMovieDetails);
                if(storedMovieDetails.getFavorite() == 1) {
                    favoriteMovieDetailsList.add(storedMovieDetails);
                }
            }
        }
    }

    /**
     * Stored movie details if any for the requested movie id.
     *
     * @param movieId Movie ID
     * @return Stored movie details or null otherwise
     */
    MovieDetails getStoredMovieDetails(String movieId){
        for(MovieDetails storeMovieDetails : storedMovieDetailsList){
            if(storeMovieDetails.getMovieId().equalsIgnoreCase(movieId)){
                return storeMovieDetails;
            }
        }
        return null;
    }

    public int getLastSelectedMoviePosition(){
        return this.lastSelectedMoviePosition;
    }

    public void setLastSelectedMoviePosition(int lastSelectedMoviePosition){
        this.lastSelectedMoviePosition = lastSelectedMoviePosition;
    }

    /**
     * Async Task which makes a call to the chosen Content Api and updates the display with the movie listing.
     *
     */
    class MovieDetailsRetriever extends AsyncTask<String, Void, List<MovieDetails>> {

        @Override
        protected void onPreExecute() {
            isContentApiFetchInProgress = true;
            progressBarFrame.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            sortTitle.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected List<MovieDetails> doInBackground(String... params) {
            String sortPreference = params[0];
            return ContentApiHandlerFactory.handler(ContentApi.valueOf(getString(R.string.default_api))).getMovieDetails(sortPreference);
        }

        @Override
        protected void onPostExecute(List<MovieDetails> movieDetails) {
            isContentApiFetchInProgress = false;
            movieDetailsList = movieDetails;
            updateDisplay(movieDetailsList);
        }

    }

    /**
     * Async Task which makes a call to the chosen Content Api and gets all the movie details.
     *
     */
    class FullMovieDetailsRetriever extends AsyncTask<MovieDetails, Void, MovieDetails> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected MovieDetails doInBackground(MovieDetails... params) {
            MovieDetails firstMovieDtls = params[0];
            if (getStoredMovieDetails(firstMovieDtls.getMovieId()) == null) {
                Log.d(MOVIE_LISTING, "Inside FullMovieDetailsRetriever making fresh api call ... ");
                return ContentApiHandlerFactory.handler(ContentApi.valueOf("TMDB")).getFullMovieInfo(firstMovieDtls.getMovieId());
            }
            else {
                Log.d(MOVIE_LISTING, "Inside FullMovieDetailsRetriever sending stored details ... ");
                return getStoredMovieDetails(firstMovieDtls.getMovieId());
            }
        }

        @Override
        protected void onPostExecute(MovieDetails fullMovieDetails) {
            if(fullMovieDetails!=null) {
                if(lastSelectedMoviePosition == 0)
                    firstMovieDetails = fullMovieDetails;
                if(mTwoPane) {
                    //Loads the details of first movie in the grid or the last selected movie in the previous orientation.
                    Callback callback = (Callback) getActivity();
                    callback.onMovieSelected(fullMovieDetails);
                }
            }
        }
    }
}
