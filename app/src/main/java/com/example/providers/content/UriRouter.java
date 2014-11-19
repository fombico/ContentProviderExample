package com.example.providers.content;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseArray;

import com.example.providers.Logger;

import java.util.List;

public class UriRouter {

    private static UriRouter sUriRouter;

    private int mUriIndex;
    private UriMatcher mUriMatcher;
    private SparseArray<UriRoute> mUriRoutes;

    private UriRouter() {
        mUriIndex = UriMatcher.NO_MATCH;
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriRoutes = new SparseArray<UriRoute>();
    }

    public static UriRouter getInstance() {
        if (sUriRouter == null) {
            sUriRouter = new UriRouter();
        }

        return sUriRouter;
    }

    public void addRoute(UriRoute uriRoute) {
        for (String path : uriRoute.getPaths()) {
            mUriIndex++;
            mUriMatcher.addURI(MusicProvider.AUTHORITY, path, mUriIndex);
            mUriRoutes.put(mUriIndex, uriRoute);
        }
    }

    public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).insert(db, uri, values);
        }

        Logger.d("Uri Router - Insert failed - no route for " + uri);
        return null;
    }

    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).delete(db, uri, selection, selectionArgs);
        }

        Logger.d("Uri Router - Delete failed - no route for " + uri);
        return 0;
    }

    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).update(db, uri, values, selection, selectionArgs);
        }

        Logger.d("Uri Router - Update failed - no route for " + uri);
        return 0;
    }

    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).query(db, uri, projection, selection, selectionArgs, sortOrder);
        }

        Logger.d("Uri Router - Query failed - no route for " + uri);
        return null;
    }

    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).getType(uri);
        }

        Logger.d("Uri Router - Get Type failed - no route for " + uri);
        return null;
    }

    public int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        int match = mUriMatcher.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            return mUriRoutes.get(match).bulkInsert(db, uri, values);
        }

        Logger.d("Uri Router - Bulk Insert failed - no route for " + uri);
        return 0;
    }


    public interface UriRoute {
        public List<String> getPaths();

        public String getType(Uri uri);

        public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
        public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values);
        public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs);
        public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs);
        public int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values);
    }

}
