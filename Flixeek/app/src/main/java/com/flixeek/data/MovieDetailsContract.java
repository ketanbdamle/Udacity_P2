package com.flixeek.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class that contains constant definitions for the URIs, column names, MIME types, and other meta-data pertaining the provider.
 * The class establishes a movie details contract between the provider and other applications.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class MovieDetailsContract {

    public static final String CONTENT_AUTHORITY = "com.flixeek";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE_DETAILS = "movie_details";

    /* Inner class that defines the table contents of the movie details table */
    public static final class MovieDetailsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAILS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;

        public static final String TABLE_NAME = "movie_details";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "movie_title";

        public static final String COLUMN_TAGLINE = "movie_tagline";

        public static final String COLUMN_OVERVIEW = "movie_overview";

        public static final String COLUMN_RUNTIME = "movie_runtime";

        public static final String COLUMN_YEAR_OF_RELEASE = "movie_year_of_release";

        public static final String COLUMN_RELEASE_DATE = "movie_release_date";

        public static final String COLUMN_GENRES = "movie_genres";

        public static final String COLUMN_THUMBNAIL = "movie_thumbnail";

        public static final String COLUMN_FANART = "movie_fanart";

        public static final String COLUMN_MOVIE_CERTIFICATE = "movie_certificate";

        public static final String COLUMN_POPULARITY = "movie_popularity";

        public static final String COLUMN_RATING = "movie_rating";

        public static final String COLUMN_VOTES = "movie_votes";

        public static final String COLUMN_HOMEPAGE = "movie_homepage";

        public static final String COLUMN_WATCHERS = "movie_watchers";

        public static final String COLUMN_MISC_INFO = "movie_json_misc_info";

        public static final String COLUMN_IS_FAVORITE = "movie_favorite";

        public static final String COLUMN_DATE_OF_CREATION = "creation_date";

        public static Uri buildMovieDetailsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildMovieDetailsByIdUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
