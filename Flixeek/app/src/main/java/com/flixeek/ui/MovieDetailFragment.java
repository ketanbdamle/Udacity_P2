package com.flixeek.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.flixeek.R;
import com.flixeek.common.FlixeekConstants;
import com.flixeek.contentapi.ContentApi;
import com.flixeek.contentapi.ContentApiHandlerFactory;
import com.flixeek.contentapi.model.MovieDetails;
import com.flixeek.contentapi.model.ReviewDetails;
import com.flixeek.contentapi.model.TrailerDetails;
import com.flixeek.data.MovieDetailsContract;
import com.flixeek.utils.DisplayUtils;
import com.flixeek.utils.FlixeekDbUtils;
import com.flixeek.utils.FlixeekUtils;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 *  *
 * @version 1.0
 * @author Ketan Damle
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_DETAIL_FRAGMENT = MovieDetailFragment.class.getName();

    private static final int MOVIE_DETAIL_LOADER = 1;

    @Bind(R.id.detailsProgressBarFrame)
    FrameLayout detailsProgressBarFrame;

    @Bind(R.id.detailsProgressBar)
    ProgressBar detailsProgressBar;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    @Bind(R.id.backdrop)
    ImageView backdrop;

    @Bind(R.id.detail_toolbar)
    Toolbar detailToolbar;

    @Bind(R.id.movie_detail_scrollview)
    NestedScrollView nestedScrollView;

    @Bind(R.id.movieThumb)
    ImageView movieThumb;

    @Bind(R.id.movieTitle)
    TextView movieTitle;

    @Bind(R.id.moviePopularity)
    TextView moviePopularity;

    @Bind(R.id.movieRatingBar)
    RatingBar movieRatingBar;

    @Bind(R.id.movieVotes)
    TextView movieVotes;

    @Bind(R.id.movieReleaseDate)
    TextView movieReleaseDate;

    @Bind(R.id.movieDescription)
    TextView movieDescription;

    @Bind(R.id.trailerContainer)
    LinearLayout trailerContainer;

    @Bind(R.id.trailerFlipper)
    ViewFlipper trailerFlipper;

    @Bind(R.id.reviewContainer)
    LinearLayout reviewContainer;

    @Bind(R.id.fab_toggle_favorite)
    FloatingActionButton fabToggleFavorite;

    private int isFavorite = 0;
    private boolean mTwoPane;
    private ShareActionProvider mShareActionProvider;
    private String localThumbnailUrl;
    private boolean isPresentInDb = false;

    private boolean isBackDropNotLoaded = false;

    MovieDetails movieDetails;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null || !savedInstanceState.containsKey(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS)){
            movieDetails = getArguments().getParcelable(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS);
        }
        else{
            movieDetails = savedInstanceState.getParcelable(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS);
        }

        if (getActivity().findViewById(R.id.movie_list) != null) {
            // The recycler view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.i(MOVIE_DETAIL_FRAGMENT, "Multi Pane: " + true);
            mTwoPane = true;
        }
        else{
            Log.i(MOVIE_DETAIL_FRAGMENT, "Multi Pane: " + false);
            mTwoPane = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ButterKnife.bind(this, rootView);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);

        if(!mTwoPane){
            /*
                The toolbar and action bar are shown in Detail Activity when not in multi pane view.
                Also display the home as up button.
             */
            detailToolbar.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setContentScrim((getResources().getDrawable(R.color.colorPrimary)));
            ((AppCompatActivity)getActivity()).setSupportActionBar(detailToolbar);
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("");
                actionBar.show();
            }
            Log.i(MOVIE_DETAIL_FRAGMENT, "Setting setDisplayHomeAsUpEnabled: " + true);
        }

        /* The overriding is essential so that in the expanded mode the Title is not shown
           and in the collapsed mode the Title is placed in the Action bar.
           Credits: Stackoverflow - unfortunately the link was not bookmarked.
         */
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //activity.setActionBarTitle(movieDetails.getTitle());
                    collapsingToolbarLayout.setTitle(movieDetails.getTitle());
                    collapsingToolbarLayout.setTitleEnabled(true);
                    if (isBackDropNotLoaded) {
                        //Content Scrim needed, else the error placeholder image collapses and along with the title looks weird in absence of the
                        //backdrop image.
                        collapsingToolbarLayout.setContentScrim((getResources().getDrawable(R.color.colorPrimary)));
                    }
                    isShow = true;
                } else if (isShow) {
                    //activity.setActionBarTitle("");
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        //Set collapsible backdrop
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitleEnabled(false);
            if(movieDetails!=null) {
                Log.i(MOVIE_DETAIL_FRAGMENT, "Loading backdrop from: " + movieDetails.getFanartUrl());
                Picasso.with(getContext())
                        .load(movieDetails.getFanartUrl())
                        .fit()
                        .placeholder(R.drawable.backdrop_default)
                        .error(R.drawable.backdrop_noimg)
                        .into(backdrop, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (movieDetails.getFanartUrl().startsWith("http")) {
                                    Bitmap bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap();
                                    String filePath = saveToInternalStorage(backdrop.getContext(), movieDetails.getMovieId(), "backdrop", bitmap);
                                    Log.i(MOVIE_DETAIL_FRAGMENT, "backdrop filepath: " + filePath);
                                    movieDetails.setFanartUrl("file://" + filePath);
                                }
                            }

                            @Override
                            public void onError() {
                                isBackDropNotLoaded = true;
                            }
                        });
                movieTitle.setText(movieDetails.getTitle());
            }
        }

        if(movieDetails!=null) {
            localThumbnailUrl = movieDetails.getListingThumbnailUrl();
            fillMovieDetailsTemplate(movieDetails);
            Log.i(MOVIE_DETAIL_FRAGMENT, "localThumbnailUrl after: " + localThumbnailUrl);
            movieDetails.setListingThumbnailUrl(localThumbnailUrl);
            Log.i(MOVIE_DETAIL_FRAGMENT, "movieDetails ThumbnailUrl after: " + movieDetails.getListingThumbnailUrl());
        }


        fabToggleFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(MOVIE_DETAIL_FRAGMENT, "fabToggleFavorite onClick ....... ");
                Log.d(MOVIE_DETAIL_FRAGMENT, "isFavorite ....... " + isFavorite);
                if (isFavorite == 1) {
                    isFavorite = 0;
                    Snackbar.make(view, movieDetails.getTitle() + " removed from Favorites Collection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    fabToggleFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                } else {
                    Snackbar.make(view, movieDetails.getTitle() + " added to Favorites Collection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    fabToggleFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_off));
                    isFavorite = 1;
                }
                performDbTasks();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //The JSON info represents additional movies details such as the trailers and reviews, if stored then don't make fresh api call.
        if (movieDetails.getJsonInfo() == null) {
            Log.d(MOVIE_DETAIL_FRAGMENT, "Inside onStart - Invoking FullMovieDetailsRetriever ... ");
            new FullMovieDetailsRetriever().execute();
        }
        else{
            Log.d(MOVIE_DETAIL_FRAGMENT, "Inside onStart - else - jsoninfo present ...");
            fillMiscMovieDetails(movieDetails);
        }
    }

    /**
     * Adds a share action provider to the menu.
     *
     * @param menu Menu to which share action would be added.
     * @param inflater Inflater for the menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (!movieDetails.getTrailerDetails().isEmpty()) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent(movieDetails.getTrailerDetails().get(0), "Share First Video"));
        }
    }


    /**
     * Hides the share action provider in case no trailers are available for a movie.
     *
     * @param menu The menu to which share action provider is attached.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mTwoPane && menu.findItem(R.id.action_share)!=null){
            if(movieDetails.getTrailerDetails().isEmpty()) {
                menu.findItem(R.id.action_share).setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(getActivity())
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(getActivity(), upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS, movieDetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(MOVIE_DETAIL_FRAGMENT, "Inside onStop ... mTwoPane: " + mTwoPane);
        performDbTasks();

    }

    /**
     * Populates the movie details.
     *  @param movieDetails Object containing all the movie details to be displayed.
     *
     */
    private void fillMovieDetailsTemplate(final MovieDetails movieDetails) {

        isFavorite = movieDetails.getFavorite();

        Log.i(MOVIE_DETAIL_FRAGMENT, "Inside movie details fragment ... ");

        movieDescription.setText(movieDetails.getOverview());
        Log.i(MOVIE_DETAIL_FRAGMENT, "Loading thumb from: " + movieDetails.getListingThumbnailUrl());
        Picasso.with(getContext())
                .load(movieDetails.getListingThumbnailUrl())
                .into(movieThumb, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (movieDetails.getListingThumbnailUrl().startsWith("http")) {
                            Bitmap bitmap = ((BitmapDrawable) movieThumb.getDrawable()).getBitmap();
                            String filePath = saveToInternalStorage(movieThumb.getContext(), movieDetails.getMovieId(), "thumb", bitmap);
                            Log.i(MOVIE_DETAIL_FRAGMENT, "thumb filepath: " + filePath);
                            localThumbnailUrl = "file://" + filePath;
                            Log.i(MOVIE_DETAIL_FRAGMENT, "localThumbnailUrl inside callback: " + localThumbnailUrl);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });

        moviePopularity.setText(String.format("%.2f", movieDetails.getPopularity()));

        if (movieDetails.getRating() > 0) {
            movieRatingBar.setRating((float) ((movieDetails.getRating()) / 2));
        }
        movieVotes.setText(movieDetails.getVotes());

        String releaseDate = FlixeekUtils.getFormattedRelease(movieDetails.getReleaseDate());
        if (releaseDate!=null && !releaseDate.equals("")) {
            movieReleaseDate.setText(releaseDate);
        }


    }

    /**
     *  Populates the miscellaneous movie details like the reviews and trailers.
     *  @param movieDetails Object containing all the movie details to be displayed.
     *
     */
    private void fillMiscMovieDetails(MovieDetails movieDetails) {

        List<TrailerDetails> trailerDetails = movieDetails.getTrailerDetails();
        List<ReviewDetails> reviewDetails = movieDetails.getReviewDetails();

        Context context = getContext();

        //Check for null context, if activity is detached before async task returns the miscellaneous movie details
        if(context!=null) {
            LayoutInflater inflater = null;
            if (!trailerDetails.isEmpty() || !reviewDetails.isEmpty()) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            Log.i(MOVIE_DETAIL_FRAGMENT, "Trailer Details size: " + trailerDetails.size());
            //Populate Trailer Details
            for (final TrailerDetails trailerDetail : trailerDetails) {
                if (inflater != null) {
                    View trailerView = inflater.inflate(R.layout.trailer_layout, trailerFlipper, false);
                    View trailerFrame = trailerView.findViewById(R.id.demoTrailerFrame);
                    trailerFrame.setId(Integer.parseInt(movieDetails.getMovieId()));
                    ImageView trailerImage = (ImageView) trailerView.findViewById(R.id.demoTrailerImage);
                    TextView trailerName = (TextView) trailerView.findViewById(R.id.demoTrailerName);
                    TextView trailerSite = (TextView) trailerView.findViewById(R.id.demoTrailerSite);
                    TextView trailerShare = (TextView) trailerView.findViewById(R.id.trailerShare);
                    TextView trailerPrevious = (TextView) trailerView.findViewById(R.id.trailerPrevious);
                    TextView trailerNext = (TextView) trailerView.findViewById(R.id.trailerNext);

                    trailerImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Trailer: " + trailerDetail.getName(), Toast.LENGTH_SHORT).show();
                            playTrailer(trailerDetail);
                        }
                    });

                    int trailerThumbnailWidth = 200;
                    int trailerThumbnailHeight = 150;

                    if (mTwoPane) {
                        trailerThumbnailWidth = 320;
                        trailerThumbnailHeight = 240;
                    } else {
                        if (getActivity() != null) {
                            if (DisplayUtils.getScreenWidth(getActivity().getWindowManager()) > 1080) {
                                trailerThumbnailWidth = 240;
                                trailerThumbnailHeight = 180;
                            }
                        }
                    }

                    final String trailerThumbnailUri = "https://i.ytimg.com/vi/" + trailerDetail.getKey() + "/0.jpg";
                    Picasso.with(getContext())
                            .load(trailerThumbnailUri)
                            .placeholder(R.drawable.ic_trailer_default_thumbnail)
                            .resize(DisplayUtils.getPixelValue(getContext(), trailerThumbnailWidth), DisplayUtils.getPixelValue(getContext(), trailerThumbnailHeight)).into(trailerImage);
                    trailerName.setText(trailerDetail.getName());
                    trailerSite.setText(Html.fromHtml("<b>Source:</b> " + trailerDetail.getSite()));

                    trailerShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(createShareTrailerIntent(trailerDetail, "Share Video"));
                        }
                    });

                    trailerPrevious.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (trailerFlipper.isFlipping()) {
                                trailerFlipper.stopFlipping();
                            }
                            trailerFlipper.showPrevious();
                        }
                    });

                    trailerNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (trailerFlipper.isFlipping()) {
                                trailerFlipper.stopFlipping();
                            }
                            trailerFlipper.showNext();
                        }
                    });

                    if (trailerDetails.size() == 1) {
                        trailerPrevious.setVisibility(View.GONE);
                        trailerNext.setVisibility(View.GONE);
                    }
                    trailerFlipper.addView(trailerView);
                }
            }

            if (!trailerDetails.isEmpty()) {
                trailerFlipper.setDisplayedChild(1);
                trailerFlipper.startFlipping();
                trailerFlipper.setFlipInterval(1500);
                trailerContainer.setVisibility(View.VISIBLE);
            }

            //Populate Review Details
            Log.i(MOVIE_DETAIL_FRAGMENT, "Review Details size: " + reviewDetails.size());
            for (final ReviewDetails reviewDetail : reviewDetails) {
                if (inflater != null) {
                    View reviewView = inflater.inflate(R.layout.review_layout, reviewContainer, false);
                    View trailerFrame = reviewView.findViewById(R.id.singleReviewContainer);
                    trailerFrame.setId(Integer.parseInt(movieDetails.getMovieId()));
                    TextView reviewAuthor = (TextView) reviewView.findViewById(R.id.reviewAuthor);
                    TextView reviewContent = (TextView) reviewView.findViewById(R.id.reviewContent);
                    TextView reviewUrl = (TextView) reviewView.findViewById(R.id.reviewUrl);

                    reviewAuthor.setText(reviewDetail.getAuthor());
                    reviewContent.setText(reviewDetail.getContent());
                    reviewUrl.setClickable(true);
                    reviewUrl.setText(context.getString(R.string.review_url_prefix_text, reviewDetail.getUrl()));
                    reviewContainer.addView(reviewView);
                }
            }

            if (!reviewDetails.isEmpty()) {
                reviewContainer.setVisibility(View.VISIBLE);
            }

            //Set favorite icons by looking up if the movie is marked as favorite
            if (movieDetails.getFavorite() == 1) {
                fabToggleFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_off));
            } else {
                fabToggleFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
            }

            //Set share action intent, only if, the trailer details are present.
            Log.i(MOVIE_DETAIL_FRAGMENT, "fillMiscMovieDetails mShareActionProvider: " + mShareActionProvider);
            if (mShareActionProvider != null && !trailerDetails.isEmpty()) {
                mShareActionProvider.setShareIntent(createShareTrailerIntent(trailerDetails.get(0), "Share First Video"));
            }
        }
    }


    /**
     *
     * @param trailerDetails Trailer Details of the movie.
     * @return Share intent for sharing any trailer.
     */
    private Intent createShareTrailerIntent(TrailerDetails trailerDetails, String intentTitle) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        else{
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, trailerDetails.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://youtube.com/watch?v=" + trailerDetails.getKey());
        return Intent.createChooser(shareIntent, intentTitle);
    }

    private void performDbTasks() {
        //Json info represents trailers and reviews data, by the below check, it is ensured that only movies with full details are stored to the database.
        if(movieDetails.getJsonInfo()!=null) {
            if (movieDetails.getCreationDate() == null && !isPresentInDb) { //additional checks for unique integrity, on extreme clicking of movie thumbnails.
                Log.d(MOVIE_DETAIL_FRAGMENT, "performDbTasks - thunmbnail url: " + localThumbnailUrl);
                Log.d(MOVIE_DETAIL_FRAGMENT, "performDbTasks - insert block: " + movieDetails.getTitle());
                movieDetails.setListingThumbnailUrl(localThumbnailUrl);
                movieDetails.setCreationDate(FlixeekUtils.getTodayDate());
                movieDetails.setFavorite(isFavorite);
                FlixeekDbUtils.saveMovieDetails(getContext().getContentResolver(), movieDetails);
            } else if (movieDetails.getFavorite() != isFavorite && isPresentInDb) {
                movieDetails.setFavorite(isFavorite);
                Log.i(MOVIE_DETAIL_FRAGMENT, "performDbTasks - update block: " + movieDetails.getTitle());
                FlixeekDbUtils.updateMovieDetails(getContext().getContentResolver(), movieDetails);
            }
        }
    }


    /**
     * Plays the movie trailer either using youtube player api, or youtube app, or a webview using iframe.
     * @param trailerDetail Object containing all the trailer details.
     *
     */
    private void playTrailer(TrailerDetails trailerDetail) {
        if (trailerDetail.getKey() != null) {
            String trailerLink = "http://youtube.com/watch?v="+trailerDetail.getKey();
            String videoId = trailerLink.substring(trailerLink.indexOf("=") + 1);
            if (YouTubeIntents.isYouTubeInstalled(getContext())) {
                Log.d(MOVIE_DETAIL_FRAGMENT, "Youtube is installed ....");
                if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext()) == YouTubeInitializationResult.SUCCESS) {
                    Log.d(MOVIE_DETAIL_FRAGMENT, "Youtube service available, playing via Youtube activity.");
                    // start the YouTube player
                    Intent youtubeIntent = new Intent(getContext(), YoutubePlayerViewActivity.class);
                    youtubeIntent.putExtra("VIDEO_ID", videoId);
                    startActivity(youtubeIntent);
                } else if (YouTubeIntents.canResolvePlayVideoIntent(getContext())) {
                    Log.d(MOVIE_DETAIL_FRAGMENT, "Playing via Youtube app ");
                    // Start an intent to the YouTube app
                    this.startActivity(
                            YouTubeIntents.createPlayVideoIntent(getContext(), videoId));
                }
            } else {
                Log.d(MOVIE_DETAIL_FRAGMENT, "Youtube is not installed, fallback to iframe view .... ");
                Intent youtubeIntent = new Intent(getContext(), TrailerWebViewActivity.class);
                youtubeIntent.putExtra(FlixeekConstants.BUNDLE_KEY_TRAILER_DETAILS, trailerDetail);
                startActivity(youtubeIntent);
            }
        }
    }

    /**
     *  Saves the image bitmap locally to a file.
     *
     *  @param context Current context
     *  @param movieId Movie Id corresponding to the image.
     *  @param imageType Image type, whether a listing thumbnail or a backdrop image.     *
     */
    private String saveToInternalStorage(Context context, String movieId, String imageType, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);

        File directory = cw.getDir(cw.getString(R.string.images_dir), Context.MODE_PRIVATE);

        File file = new File(directory.getPath(), movieId+"_"+imageType+".jpeg");
        Log.d(MOVIE_DETAIL_FRAGMENT, "Image Local Path: " + file.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            Log.e(MOVIE_DETAIL_FRAGMENT, "Exception occurred while saving image locally for movie id: " + movieId + ", the exception message: " + e.getMessage());
        } finally {
            try {
                if(fos!=null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(MOVIE_DETAIL_FRAGMENT, "IOException occurred while saving image locally for movie id: "+ movieId+", the exception message: "+e.getMessage());
            }
        }
        Log.d(MOVIE_DETAIL_FRAGMENT, "Directory Path: "+directory.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * Creates a loader wchi fetches the persisted movie details
     *
     * @param id Loader ID
     * @param args Arguments to the Loader, if any.
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(MOVIE_DETAIL_FRAGMENT, "Inside onCreateLoader ... ");
        return new CursorLoader(getContext(),
                MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsByIdUri(movieDetails.getMovieId()),
                new String[]{MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        isPresentInDb = data.moveToFirst();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Async Task which makes a call to the chosen Content Api and gets all the movie details.
     *
     */
    class FullMovieDetailsRetriever extends AsyncTask<String, Void, MovieDetails> {

        @Override
        protected void onPreExecute() {
            detailsProgressBarFrame.setVisibility(View.VISIBLE);
            detailsProgressBar.setVisibility(View.VISIBLE);
            fabToggleFavorite.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.GONE);
        }

        @Override
        protected MovieDetails doInBackground(String... params) {
            return ContentApiHandlerFactory.handler(ContentApi.valueOf("TMDB")).getFullMovieInfo(movieDetails.getMovieId());
        }

        @Override
        protected void onPostExecute(MovieDetails allMovieDetails) {
            if(isAdded()) {
                if (allMovieDetails != null) {
                    allMovieDetails.setCreationDate(movieDetails.getCreationDate());
                    allMovieDetails.setFavorite(movieDetails.getFavorite());
                    movieDetails = allMovieDetails;
                    fillMiscMovieDetails(movieDetails);
                }
                detailsProgressBarFrame.setVisibility(View.GONE);
                detailsProgressBar.setVisibility(View.GONE);
                fabToggleFavorite.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }

        }

    }
}
