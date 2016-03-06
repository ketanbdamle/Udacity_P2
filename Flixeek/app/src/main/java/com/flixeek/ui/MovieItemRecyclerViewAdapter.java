package com.flixeek.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flixeek.R;
import com.flixeek.common.FlixeekConstants;
import com.flixeek.common.SortPrefEnum;
import com.flixeek.contentapi.model.MovieDetails;
import com.flixeek.utils.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for the recycler view holding the movie items.
 *
 * @version 1.0
 * @author Ketan Damle
 */
public class MovieItemRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieItemRecyclerViewAdapter.ViewHolder> {

    private static final String RECYCLERVIEW_ADAPTER = MovieItemRecyclerViewAdapter.class.getName();

    private final MovieListFragment movieListFragment;
    private List<MovieDetails> movieDetailsList;
    private String currSortPreference;
    private final int thumbnailWidth;
    private final int thumbnailHeight;
    private final boolean mTwoPane;
    private final MovieListFragment.Callback callback;

    public MovieItemRecyclerViewAdapter(MovieListFragment movieListFragment, List<MovieDetails> movieDetailsList, String currSortPreference, boolean mTwoPane, MovieListFragment.Callback callback) {
        this.movieListFragment = movieListFragment;
        this.movieDetailsList = movieDetailsList;
        this.currSortPreference = currSortPreference;
        thumbnailWidth = DisplayUtils.getPixelValue(movieListFragment.getActivity(), FlixeekConstants.DEFAULT_GRID_IMG_WIDTH);
        thumbnailHeight =DisplayUtils.getPixelValue(movieListFragment.getActivity(), FlixeekConstants.DEFAULT_GRID_IMG_HEIGHT);
        this.mTwoPane = mTwoPane;
        this.callback = callback;
        Log.i(RECYCLERVIEW_ADAPTER, "Multi Pane: " + mTwoPane);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MovieDetails movieDetails = movieDetailsList.get(position);

        Picasso.with(movieListFragment.getActivity())
                .load(movieDetails.getListingThumbnailUrl())
                .placeholder(R.drawable.listing_default_img)
                .error(R.drawable.ic_no_image)
                .resize(thumbnailWidth, thumbnailHeight)
                .into(holder.thumbnail);

        if(currSortPreference.equals(SortPrefEnum.popularity.name())){
            Picasso.with(movieListFragment.getActivity()).load(R.drawable.ic_social_whatshot).into(holder.movieTrendImg);
            holder.movieTrendText.setText(String.format("%.2f", movieDetails.getPopularity()));
        }
        else{
            Picasso.with(movieListFragment.getActivity()).load(R.drawable.ic_toggle_star).into(holder.movieTrendImg);
            holder.movieTrendText.setText(String.format("%.2f", movieDetails.getRating()));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(RECYCLERVIEW_ADAPTER, "Inside onclick in recycler adapter - movie clicked - " + movieDetails.getTitle());
                MovieDetails tmpMovieDetails = movieListFragment.getStoredMovieDetails(movieDetails.getMovieId());
                if (tmpMovieDetails == null) {
                    tmpMovieDetails = movieDetails;
                    Log.i(RECYCLERVIEW_ADAPTER, "Stored details absent .... ");
                } else {
                    Log.i(RECYCLERVIEW_ADAPTER, "Sending stored details .... ");
                }
                movieListFragment.setLastSelectedMoviePosition(position);
                Log.i("Trial Check", "Inside onclick in recycler adapter - tmpMovieDetails title - " + tmpMovieDetails.getTitle());
                callback.onMovieSelected(tmpMovieDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;

        @Bind(R.id.thumbnail)
        ImageView thumbnail;

        @Bind(R.id.movieTrendImg)
        ImageView movieTrendImg;

        @Bind(R.id.movieTrendText)
        TextView movieTrendText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public void setMovieDetailsList(List<MovieDetails> movieDetailsList) {
        this.movieDetailsList = movieDetailsList;
    }

    public void setCurrSortPreference(String currSortPreference) {
        this.currSortPreference = currSortPreference;
    }
}