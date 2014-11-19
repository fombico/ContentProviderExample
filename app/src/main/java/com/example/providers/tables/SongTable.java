package com.example.providers.tables;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.providers.Logger;
import com.example.providers.content.MusicProvider;

import java.util.ArrayList;
import java.util.List;

public class SongTable extends Table {

    public static final String TABLE_NAME = "song";
    public static final Uri URI = Uri.parse("content://" + MusicProvider.AUTHORITY + "/" + TABLE_NAME);

    public static class Columns extends Table.Columns {
        public static final String SONG_NAME = "songName";
    }

    private static final String CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME
            + "(" + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Columns.SONG_NAME + " TEXT NOT NULL, "
            + "UNIQUE (" + Columns.SONG_NAME + ") ON CONFLICT REPLACE);";

    static final int SONG_DIR = 1;
    static final int SONG_ITEM = 2;
    static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MusicProvider.AUTHORITY, TABLE_NAME, SONG_DIR);
        sUriMatcher.addURI(MusicProvider.AUTHORITY, TABLE_NAME + "/#", SONG_ITEM);
    }

    private static SongTable sTable;

    private SongTable() {

    }

    public static SongTable getInstance() {
        if (sTable == null) {
            sTable = new SongTable();
        }
        return sTable;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getCreateSQL() {
        return CREATE_SQL;
    }

    @Override
    public List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(TABLE_NAME);
        paths.add(TABLE_NAME + "/#");
        return paths;
    }

    @Override
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("Querying song table with uri: " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName());

        switch (sUriMatcher.match(uri)) {
            case SONG_DIR:
                break;
            case SONG_ITEM:
                qb.appendWhere(Columns._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = Columns.SONG_NAME;
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        /** register to watch a content URI for changes **/
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Logger.d("Queried song table, cursor size: " + cursor.getCount());

        return cursor;
    }

    @Override
    public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        Logger.d("Inserting into song table with uri " + uri.toString());
        long rowId = db.insert(getTableName(), null, values);
        if (rowId > 0) {
            Logger.d("Inserted row " + rowId + " into song table");
            Uri rowUri = ContentUris.withAppendedId(URI, rowId);
            getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        Logger.d("Failed to insert to song table");
        return null;
    }

    @Override
    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        Logger.d("Deleting from song table with uri " + uri.toString());
        int count = 0;

        switch (sUriMatcher.match(uri)) {
            case SONG_DIR:
                count = db.delete(getTableName(), selection, selectionArgs);
                break;
            case SONG_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.delete(getTableName(), Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Deleted " + count + " from song table");
        return count;
    }

    @Override
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("Updating song table with uri: " + uri.toString());
        int count = 0;
        switch (sUriMatcher.match(uri)){
            case SONG_DIR:
                count = db.update(getTableName(), values, selection, selectionArgs);
                break;
            case SONG_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.update(getTableName(), values, Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Updating " + count + " from song table");
        return count;
    }
}
