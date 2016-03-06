package com.flixeek.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flixeek.data.MovieDetailsContract.MovieDetailsEntry;

/**
 * Database Helper class which creates the database, and recreates it in case of newer versions.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class MovieDetailsDbHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "flixeek_movies.db";

    public MovieDetailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_DETAILS = "CREATE TABLE "+ MovieDetailsEntry.TABLE_NAME + "(" +
                                                    MovieDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                    MovieDetailsEntry.COLUMN_MOVIE_ID +" TEXT NOT NULL UNIQUE, " +
                                                    MovieDetailsEntry.COLUMN_TITLE +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_TAGLINE +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_OVERVIEW +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_RUNTIME +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_YEAR_OF_RELEASE +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_RELEASE_DATE +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_GENRES +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_THUMBNAIL +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_FANART +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_MOVIE_CERTIFICATE +" REAL, " +
                                                    MovieDetailsEntry.COLUMN_POPULARITY +" REAL NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_RATING +" REAL NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_VOTES +" INTEGER NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_HOMEPAGE +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_WATCHERS +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_MISC_INFO +" TEXT, " +
                                                    MovieDetailsEntry.COLUMN_IS_FAVORITE +" TEXT NOT NULL, " +
                                                    MovieDetailsEntry.COLUMN_DATE_OF_CREATION +" TEXT NOT NULL" +
                                                ");";

        db.execSQL(SQL_CREATE_MOVIE_DETAILS);

    }

    /**
     * Recreates the database in case of schema changes and upgrade to the database version.
     *
     * @param db SQLiteDatabase storing the movie details
     * @param oldVersion Version prior to upgrade
     * @param newVersion Version post upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDetailsEntry.TABLE_NAME);
        onCreate(db);
    }
}
