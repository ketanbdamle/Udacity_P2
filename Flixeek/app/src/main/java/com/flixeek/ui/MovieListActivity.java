package com.flixeek.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.flixeek.R;
import com.flixeek.common.FlixeekConstants;
import com.flixeek.common.SortPrefEnum;
import com.flixeek.contentapi.model.MovieDetails;

/**
 * An activity representing a list of Movies.
 *
 * @version 1.0
 * @author Ketan Damle
 */
public class MovieListActivity extends AppCompatActivity implements MovieListFragment.Callback {

    private static final String MOVIE_LISTING = MovieListActivity.class.getName();
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MOVIE_LISTING, "Inside onCreate ... ");

        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        Log.i(MOVIE_LISTING, "Inside onCreate ... mTwoPane: "+mTwoPane);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(MOVIE_LISTING, "Inside onCreateOptionsMenu ... ");
        getMenuInflater().inflate(R.menu.main_listing_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.i(MOVIE_LISTING, "Inside onPrepareOptionsMenu ... ");
        if(!mTwoPane && menu.findItem(R.id.action_share)!=null){
            menu.findItem(R.id.action_share).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MovieListFragment movieListFragment = (MovieListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list);
        if(movieListFragment!=null) {
            switch (item.getItemId()) {
                case R.id.action_sort_by_popularity:
                    movieListFragment.onSorfPrefChanged(SortPrefEnum.popularity);
                    return true;
                case R.id.action_sort_by_voter_average:
                    movieListFragment.onSorfPrefChanged(SortPrefEnum.vote_average);
                    return true;
                case R.id.action_settings:
                    movieListFragment.onSorfPrefChanged(SortPrefEnum.nopref);
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                case R.id.action_show_favorites:
                    movieListFragment.onSorfPrefChanged(SortPrefEnum.favorites);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays the movie details in a new details fragment for multi pane view, otherwise, starts a detail activity.
     *
     * @param movieDetails Details of the Movie requested.
     */
    @Override
    public void onMovieSelected(MovieDetails movieDetails) {
        Log.i(MOVIE_LISTING, "Inside onMovieSelected ... mTwoPane: "+mTwoPane);
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS, movieDetails);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(FlixeekConstants.BUNDLE_KEY_MOVIE_DETAILS, movieDetails);
            startActivity(intent);
        }
    }
}
