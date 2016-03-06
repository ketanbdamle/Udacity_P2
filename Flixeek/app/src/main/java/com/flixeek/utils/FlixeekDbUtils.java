package com.flixeek.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.flixeek.contentapi.model.MovieDetails;
import com.flixeek.data.MovieDetailsContract;

/**
 * @author Ketan Damle
 * @version 1.0
 */
public class FlixeekDbUtils {

    private static final String FLIXEEK_DB_UTILS = FlixeekDbUtils.class.getName();

    public static Uri saveMovieDetails(ContentResolver contentResolver, MovieDetails movieDetails) {
        Log.d(FLIXEEK_DB_UTILS, "Inserting ....... ");
        ContentValues movieDetailsContentValues = new ContentValues();
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID, movieDetails.getMovieId());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_TITLE, movieDetails.getTitle());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_TAGLINE, movieDetails.getTagline());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_OVERVIEW, movieDetails.getOverview());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RUNTIME, movieDetails.getRuntime());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_YEAR_OF_RELEASE, movieDetails.getYearOfRelease());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RELEASE_DATE, movieDetails.getReleaseDate());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_GENRES, movieDetails.getGenres().toString());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_THUMBNAIL, movieDetails.getListingThumbnailUrl());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_FANART, movieDetails.getFanartUrl());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_CERTIFICATE, movieDetails.getCertificate());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_POPULARITY, movieDetails.getPopularity());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RATING, movieDetails.getRating());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_VOTES, movieDetails.getVotes());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_HOMEPAGE, movieDetails.getHomepage());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_WATCHERS, movieDetails.getWatchers());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MISC_INFO, movieDetails.getJsonInfo());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_IS_FAVORITE, movieDetails.getFavorite());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_DATE_OF_CREATION, FlixeekUtils.getTodayDate());

        Uri returnUri = contentResolver.insert(MovieDetailsContract.MovieDetailsEntry.CONTENT_URI, movieDetailsContentValues);
        Log.i(FLIXEEK_DB_UTILS, "Return URI: " + (returnUri != null ? returnUri.toString() : null));

        return returnUri;
    }

    public static int deleteMovieDetails(ContentResolver contentResolver, String movieId) {
        Log.d(FLIXEEK_DB_UTILS, "Deleting ....... ");
        return contentResolver.delete(MovieDetailsContract.MovieDetailsEntry.CONTENT_URI
                , MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = ?"
                , new String[]{movieId});
    }

    public static int updateMovieDetails(ContentResolver contentResolver, MovieDetails movieDetails) {
        Log.d(FLIXEEK_DB_UTILS, "Updating ....... ");
        ContentValues movieDetailsContentValues = new ContentValues();
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID, movieDetails.getMovieId());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_TITLE, movieDetails.getTitle());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_TAGLINE, movieDetails.getTagline());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_OVERVIEW, movieDetails.getOverview());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RUNTIME, movieDetails.getRuntime());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_YEAR_OF_RELEASE, movieDetails.getYearOfRelease());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RELEASE_DATE, movieDetails.getReleaseDate());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_GENRES, movieDetails.getGenres().toString());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_THUMBNAIL, movieDetails.getListingThumbnailUrl());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_FANART, movieDetails.getFanartUrl());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_CERTIFICATE, movieDetails.getCertificate());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_POPULARITY, movieDetails.getPopularity());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RATING, movieDetails.getRating());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_VOTES, movieDetails.getVotes());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_HOMEPAGE, movieDetails.getHomepage());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_WATCHERS, movieDetails.getWatchers());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_MISC_INFO, movieDetails.getJsonInfo());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_IS_FAVORITE, movieDetails.getFavorite());
        movieDetailsContentValues.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_DATE_OF_CREATION, FlixeekUtils.getTodayDate());

        return contentResolver.update(MovieDetailsContract.MovieDetailsEntry.CONTENT_URI
                , movieDetailsContentValues
                , MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = ?"
                , new String[]{movieDetails.getMovieId()});
    }
}
