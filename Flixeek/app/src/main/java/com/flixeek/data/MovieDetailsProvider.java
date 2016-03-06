package com.flixeek.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Content Provider for accessing and manipulating the flixeek movie details database.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class MovieDetailsProvider extends ContentProvider {

    static final int MOVIE_DETAILS = 100;
    static final int MOVIE_DETAILS_BY_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDetailsDbHelper movieDetailsDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String movie_details_authority = MovieDetailsContract.CONTENT_AUTHORITY;

        matcher.addURI(movie_details_authority, MovieDetailsContract.PATH_MOVIE_DETAILS, MOVIE_DETAILS);
        matcher.addURI(movie_details_authority, MovieDetailsContract.PATH_MOVIE_DETAILS + "/*", MOVIE_DETAILS_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDetailsDbHelper = new MovieDetailsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case MOVIE_DETAILS: {
                final SQLiteDatabase db = movieDetailsDbHelper.getReadableDatabase();
                retCursor = db.query(MovieDetailsContract.MovieDetailsEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            }
            case MOVIE_DETAILS_BY_ID: {
                retCursor = getMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieDetailsByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieDetailsContract.MovieDetailsEntry.getMovieIdFromUri(uri);
        final String selectionClause = MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = ?";
        final SQLiteDatabase db = movieDetailsDbHelper.getReadableDatabase();
        return db.query(MovieDetailsContract.MovieDetailsEntry.TABLE_NAME, null, selectionClause, new String[]{movieId}, null, null, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIE_DETAILS:
                return MovieDetailsContract.MovieDetailsEntry.CONTENT_TYPE;
            case MOVIE_DETAILS_BY_ID:
                return MovieDetailsContract.MovieDetailsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDetailsDbHelper.getWritableDatabase();
        final int match = buildUriMatcher().match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_DETAILS: {
                long _id = db.insert(MovieDetailsContract.MovieDetailsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDetailsDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE_DETAILS:
                rowsDeleted = db.delete(
                        MovieDetailsContract.MovieDetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDetailsDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_DETAILS:
                rowsUpdated = db.update(MovieDetailsContract.MovieDetailsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
