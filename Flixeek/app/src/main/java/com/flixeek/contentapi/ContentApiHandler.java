package com.flixeek.contentapi;

import com.flixeek.contentapi.model.MovieDetails;

import java.util.List;

/**
 * Abstract class to be inherited by different Content APIs.
 *
 * @version 1.0
 * @author Ketan Damle
 */
public abstract class ContentApiHandler {

    /**
     * @return List of the Movies and their primary details represented by {@link MovieDetails}
     */
    public abstract List<MovieDetails> getMovieDetails(String... params);

    /**
     * @return Gets full movie information including videos and reviews besides the primary details, represented by {@link MovieDetails}
     */
    public abstract MovieDetails getFullMovieInfo(String... params);

    /**
     * @param jsonInfo JSON string object representing the movie details.
     * @return Gets full movie information including videos and reviews besides the primary details, using the json info string
     */
    public abstract MovieDetails getMovieDetailsFromJson(String jsonInfo);
}
