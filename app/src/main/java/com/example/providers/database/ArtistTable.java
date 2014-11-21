package com.example.providers.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.example.providers.content.MusicProvider;

public class ArtistTable extends SQLTable {

    public static final String TABLE_NAME = "artist";
    public static final Uri URI = Uri.parse("content://" + MusicProvider.AUTHORITY + "/" + TABLE_NAME);

    public static class Columns extends SQLTable.Columns {
        public static final String ARTIST_NAME = "artistName";
    }

    private static final String CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "(" + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Columns.ARTIST_NAME + " TEXT NOT NULL, "
            + "UNIQUE ("+ Columns.ARTIST_NAME + ") ON CONFLICT REPLACE);";

    private static ArtistTable sTable;

    private ArtistTable() {

    }

    public static ArtistTable getInstance() {
        if (sTable == null) {
            sTable = new ArtistTable();
        }
        return sTable;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Uri getUri() {
        return URI;
    }

    @Override
    public String getCreateSQL() {
        return CREATE_SQL;
    }

    @Override
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = Columns.ARTIST_NAME;
        }
        return super.query(db, uri, projection, selection, selectionArgs, sortOrder);
    }
}
