package com.example.providers;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.providers.content.AsyncQueryHandler;
import com.example.providers.database.ArtistTable;
import com.example.providers.database.GenreTable;
import com.example.providers.database.SongTable;

import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    private Random mRandom;
    private TextView mLoggerText;
    private AsyncQueryHandler mQueryHandler;

    private enum Type {
        ARTIST, GENRE, SONG
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_add_song_button).setOnClickListener(this);
        findViewById(R.id.main_remove_song_button).setOnClickListener(this);
        findViewById(R.id.main_add_many_artists_button).setOnClickListener(this);
        findViewById(R.id.main_remove_all_artists_button).setOnClickListener(this);
        findViewById(R.id.main_add_many_genre_button).setOnClickListener(this);
        findViewById(R.id.main_update_all_genre_button).setOnClickListener(this);
        findViewById(R.id.main_remove_genre_all_button).setOnClickListener(this);

        mLoggerText = (TextView) findViewById(R.id.main_logger);

        mRandom = new Random();
        mQueryHandler = getAsyncQueryHandler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_add_song_button:
                onAddSongClicked();
                break;
            case R.id.main_remove_song_button:
                onRemoveSongClicked();
                break;
            case R.id.main_add_many_artists_button:
                onAddManyArtistsClicked();
                break;
            case R.id.main_remove_all_artists_button:
                onRemoveAllArtistsClicked();
                break;
            case R.id.main_add_many_genre_button:
                onAddManyGenresClicked();
                break;
            case R.id.main_update_all_genre_button:
                onUpdateAllGenresClicked();
                break;
            case R.id.main_remove_genre_all_button:
                onRemoveAllGenresClicked();
                break;
        }
    }

    private void onAddSongClicked() {
        ContentValues values = new ContentValues();
        values.put(SongTable.Columns.SONG_NAME, "Song " + mRandom.nextInt());
        mQueryHandler.startInsert(0, Type.SONG, SongTable.URI, values);
    }

    private void onRemoveSongClicked() {
        mQueryHandler.startQuery(0, Type.SONG, SongTable.URI, null, null, null, null);
    }

    private void onAddManyArtistsClicked() {
        ContentValues[] array = new ContentValues[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = new ContentValues();
            array[i].put(ArtistTable.Columns.ARTIST_NAME, "Artist " + mRandom.nextInt());
        }
        mQueryHandler.startBulkInsert(0, Type.ARTIST, ArtistTable.URI, array);
    }

    private void onRemoveAllArtistsClicked() {
        mQueryHandler.startDelete(0, Type.ARTIST, ArtistTable.URI, null, null);
    }

    private void onAddManyGenresClicked() {
        ContentValues[] array = new ContentValues[4];
        for (int i = 0; i < array.length; i++) {
            array[i] = new ContentValues();
            array[i].put(GenreTable.Columns.GENRE_NAME, "Genre " + mRandom.nextInt());
        }
        mQueryHandler.startBulkInsert(0, Type.GENRE, GenreTable.URI, array);
    }

    private void onUpdateAllGenresClicked() {
        mQueryHandler.startQuery(0, Type.GENRE, GenreTable.URI, null, null, null, null);
    }

    private void onRemoveAllGenresClicked() {
        mQueryHandler.startDelete(0, Type.GENRE, GenreTable.URI, null, null);
    }

    private void log(String message) {
        mLoggerText.append(message + "\n");
    }


    private void onQuery(Object cookie, Cursor cursor) {
        // TODO abstract this better
        // Though normally thea  query handler instance wouldn't have to deal these many different tables
        if (cookie != null && cookie instanceof Type) {
            switch ((Type) cookie) {
                case GENRE:
                    // used for updating all genres
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(GenreTable.Columns.GENRE_NAME, "Genre " + mRandom.nextInt());
                            String where = GenreTable.Columns._ID + "=?";
                            String[] whereArgs = new String[1];
                            whereArgs[0] = "" + cursor.getInt(cursor.getColumnIndex(GenreTable.Columns._ID));
                            mQueryHandler.startUpdate(0, Type.GENRE, GenreTable.URI, contentValues, where, whereArgs);
                        }
                        cursor.close();
                    } else {
                        log("No cursor from trying to update all genres");
                    }

                    break;
                case SONG:
                    // used for remove
                    if (cursor == null) {
                        log("Empty song table");
                    } else {
                        if (cursor.moveToLast()) {
                            int id = cursor.getInt(cursor.getColumnIndex(SongTable.Columns._ID));
                            mQueryHandler.startDelete(0, Type.SONG, Uri.withAppendedPath(SongTable.URI, "" + id), null, null);
                        }
                        cursor.close();
                    }
                    break;
            }
        }
    }

    private AsyncQueryHandler getAsyncQueryHandler() {
        return new AsyncQueryHandler(getContentResolver()) {

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                onQuery(cookie, cursor);
            }

            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                if (cookie != null && cookie instanceof Type) {
                    switch ((Type) cookie) {
                        case ARTIST:
                            log("Inserted " + uri + " on artist table");
                            break;
                        case GENRE:
                            log("Inserted " + uri + " on genre table");
                            break;
                        case SONG:
                            log("Inserted " + uri + " on song table");
                            break;
                    }
                }
            }

            @Override
            protected void onUpdateComplete(int token, Object cookie, int result) {
                super.onUpdateComplete(token, cookie, result);
                if (cookie != null && cookie instanceof Type) {
                    switch ((Type) cookie) {
                        case ARTIST:
                            log("Updated " + result + " on artist table");
                            break;
                        case GENRE:
                            log("Updated " + result + " on genre table");
                            break;
                        case SONG:
                            log("Updated " + result + " on song table");
                            break;
                    }
                }
            }

            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                if (cookie != null && cookie instanceof Type) {
                    switch ((Type) cookie) {
                        case ARTIST:
                            log("Deleted " + result + " from artist table");
                            break;
                        case GENRE:
                            log("Deleted " + result + " from genre table");
                            break;
                        case SONG:
                            log("Deleted " + result + " from song table");
                            break;
                    }
                }
            }

            @Override
            protected void onBulkInsertComplete(int token, Object cookie, int result) {
                super.onBulkInsertComplete(token, cookie, result);
                if (cookie != null && cookie instanceof Type) {
                    switch ((Type) cookie) {
                        case ARTIST:
                            log("Bulk inserted " + result + " to artist table");
                            break;
                        case GENRE:
                            log("Bulk inserted " + result + " to genre table");
                            break;
                        case SONG:
                            log("Bulk inserted " + result + " to song table");
                            break;
                    }
                }
            }
        };
    }

}
