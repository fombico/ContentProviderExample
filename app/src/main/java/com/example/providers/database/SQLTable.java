package com.example.providers.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.providers.Logger;
import com.example.providers.ProvidersApplication;
import com.example.providers.content.MusicProvider;
import com.example.providers.content.UriRouter;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLTable implements UriRouter.UriRoute {

    public static class Columns {
        public static final String _ID = "_id";
    }

    protected final UriMatcher mUriMatcher;

    protected final String BASE_DIR_PATH = getTableName();
    protected final String BASE_ITEM_PATH = getTableName() + "/#";
    protected final int BASE_DIR_CODE = 1;
    protected final int BASE_ITEM_CODE = 2;

    protected SQLTable() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        initUriMatcher();
    }

    protected void initUriMatcher() {
        mUriMatcher.addURI(MusicProvider.AUTHORITY, BASE_DIR_PATH, BASE_DIR_CODE);
        mUriMatcher.addURI(MusicProvider.AUTHORITY, BASE_ITEM_PATH, BASE_ITEM_CODE);
    }

    public abstract String getTableName();

    public abstract Uri getUri();

    public abstract String getCreateSQL();

    public void onCreate(SQLiteDatabase db) {
        Logger.d("Executing: " + getCreateSQL());
        db.execSQL(getCreateSQL());

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(db);
    }

    // ========= URI ROUTE METHODS

    @Override
    public List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(BASE_DIR_PATH);
        paths.add(BASE_ITEM_PATH);
        return paths;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)){
            case BASE_DIR_CODE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + MusicProvider.AUTHORITY + "." + getTableName();
            case BASE_ITEM_CODE:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + MusicProvider.AUTHORITY + "." + getTableName();
        }
        return null;
    }

    @Override
    public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        Logger.d("Inserting into " + getTableName() + " table with uri " + uri.toString());
        long rowId = db.insert(getTableName(), null, values);
        if (rowId > 0) {
            Uri rowUri = ContentUris.withAppendedId(getUri(), rowId);
            Logger.d("Inserted row into " + getTableName() + " table, uri: " + rowUri);
            getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        Logger.d("Failed to insert to " + getTableName() + " table");
        return null;
    }

    @Override
    public int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        int count = 0;
        for(ContentValues contentValues : values) {
            Uri insertUri = insert(db, uri, contentValues);
            if (insertUri != null) {
                count++;
            }
        }

        return count;
    }


    @Override
    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        Logger.d("Deleting from " + getTableName() + " table with uri " + uri.toString());
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case BASE_DIR_CODE:
                count = db.delete(getTableName(), selection, selectionArgs);
                break;
            case BASE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                count = db.delete(getTableName(), Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Deleted " + count + " from " + getTableName() + " table");
        return count;
    }

    @Override
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("Updating " + getTableName() + " table with uri: " + uri.toString());
        int count = 0;
        switch (mUriMatcher.match(uri)){
            case BASE_DIR_CODE:
                count = db.update(getTableName(), values, selection, selectionArgs);
                break;
            case BASE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                count = db.update(getTableName(), values, Columns._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
        }
        getContentResolver().notifyChange(uri, null);
        Logger.d("Updated " + count + " from " + getTableName() + " table");
        return count;
    }


    @Override
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("Querying " + getTableName() + " table with uri: " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName());

        switch (mUriMatcher.match(uri)) {
            case BASE_DIR_CODE:
                break;
            case BASE_ITEM_CODE:
                qb.appendWhere(Columns._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        /** register to watch a content URI for changes **/
        cursor.setNotificationUri(getContentResolver(), uri);

        Logger.d("Queried " + getTableName() + " table, cursor size: " + cursor.getCount());

        return cursor;
    }

    // ====== HELPER METHODS

    protected Context getContext() {
        return ProvidersApplication.getContext();
    }

    protected ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

}
