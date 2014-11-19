package com.example.providers.tables;

import android.content.ContentResolver;
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


    private class Paths {
        static final String GENRE_DIR = TABLE_NAME;
        static final String GENRE_ITEM = TABLE_NAME + "/#";
    }

    private static final int GENRE_DIR = 1;
    private static final int GENRE_ITEM = 2;
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MusicProvider.AUTHORITY, Paths.GENRE_DIR, GENRE_DIR);
        sUriMatcher.addURI(MusicProvider.AUTHORITY, Paths.GENRE_ITEM, GENRE_ITEM);
    }

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
    public String getCreateSQL() {
        return CREATE_SQL;
    }

    @Override
    public List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(Paths.GENRE_DIR);
        paths.add(Paths.GENRE_ITEM);
        return paths;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case GENRE_DIR:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + MusicProvider.AUTHORITY + "." + GenreTable.TABLE_NAME;
            case GENRE_ITEM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + MusicProvider.AUTHORITY + "." + GenreTable.TABLE_NAME;
        }
        return super.getType(uri);
    }

    @Override
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("Querying genre table with uri: " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName());

        switch (sUriMatcher.match(uri)) {
            case GENRE_DIR:
                break;
            case GENRE_ITEM:
                qb.appendWhere(Columns._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = Columns.GENRE_NAME;
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        /** register to watch a content URI for changes **/
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Logger.d("Queried genre table, cursor size: " + cursor.getCount());

        return cursor;
    }

    @Override
    public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        Logger.d("Inserting into genre table with uri " + uri.toString());
        long rowId = db.insert(getTableName(), null, values);
        if (rowId > 0) {
            Logger.d("Inserted row " + rowId + " into genre table");
            Uri rowUri = ContentUris.withAppendedId(URI, rowId);
            getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        Logger.d("Failed to insert to genre table");
        return null;
    }

    @Override
    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        Logger.d("Deleting from genre table with uri " + uri.toString());
        int count = 0;

        switch (sUriMatcher.match(uri)) {
            case GENRE_DIR:
                count = db.delete(getTableName(), selection, selectionArgs);
                break;
            case GENRE_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.delete(getTableName(), Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Deleted " + count + " from genre table");
        return count;
    }

    @Override
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("Updating genre table with uri: " + uri.toString());
        int count = 0;
        switch (sUriMatcher.match(uri)){
            case GENRE_DIR:
                count = db.update(getTableName(), values, selection, selectionArgs);
                break;
            case GENRE_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.update(getTableName(), values, Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Updating " + count + " from genre table");
        return count;
    }
}
