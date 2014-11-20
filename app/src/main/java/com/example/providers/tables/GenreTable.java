package com.example.providers.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.example.providers.content.MusicProvider;

public class GenreTable extends Table {

    public static final String TABLE_NAME = "genre";
    public static final Uri URI = Uri.parse("content://" + MusicProvider.AUTHORITY + "/" + TABLE_NAME);

    public static class Columns extends Table.Columns {
        public static final String GENRE_NAME = "genreName";
    }

    private static final String CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME
            + "(" + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Columns.GENRE_NAME + " TEXT NOT NULL, "
            + "UNIQUE ("+ Columns.GENRE_NAME + ") ON CONFLICT REPLACE);";

    private static GenreTable sTable;

    private GenreTable() {

    }

    public static GenreTable getInstance() {
        if (sTable == null) {
            sTable = new GenreTable();
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
            sortOrder = Columns.GENRE_NAME;
        }
        return super.query(db, uri, projection, selection, selectionArgs, sortOrder);
    }
}
