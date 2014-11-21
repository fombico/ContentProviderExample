package com.example.providers.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.example.providers.Logger;
import com.example.providers.ProvidersApplication;
import com.example.providers.content.MusicProvider;
import com.example.providers.content.UriRouter;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLView implements UriRouter.UriRoute {

    private ContentObserver mContentObserver;
    private static final String CREATE_SQL_FORMAT = "CREATE VIEW IF NOT EXISTS %s AS %s;";

    public static class Columns {
        public static final String _ID = "_id";
    }

    protected final UriMatcher mUriMatcher;

    protected final String BASE_DIR_PATH = getViewName();
    protected final int BASE_DIR_CODE = 1;

    protected SQLView() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        initUriMatcher();
    }

    protected void initUriMatcher() {
        mUriMatcher.addURI(MusicProvider.AUTHORITY, BASE_DIR_PATH, BASE_DIR_CODE);
    }

    public abstract String getViewName();

    public abstract Uri getUri();

    public abstract String getSelectStatement();

    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(CREATE_SQL_FORMAT, getViewName(), getSelectStatement());
        Logger.d("Executing: " + sql);
        db.execSQL(sql);
    }

    public void initContentObserver() {
        List<Uri> urisToObserve = getUrisToObserve();
        if (urisToObserve != null) {
            Logger.d("Registering content observer");
            mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public boolean deliverSelfNotifications() {
                    return super.deliverSelfNotifications();
                }

                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                }

                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    Logger.d("onChange, uri: " + uri);
                    getContentResolver().notifyChange(getUri(), null);
                }
            };

            for (Uri uri : urisToObserve) {
                getContentResolver().registerContentObserver(uri, true, mContentObserver);
            }
        }
    }

    protected abstract List<Uri> getUrisToObserve();


    // ========= URI ROUTE METHODS

    @Override
    public List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add(BASE_DIR_PATH);
        return paths;
    }

    @Override
    public String getType(Uri uri) {
        return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + MusicProvider.AUTHORITY + "." + getViewName();
    }

    @Override
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("Querying " + getViewName() + " view with uri: " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getViewName());
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("The insert operation is not supported for SQL views");
    }

    @Override
    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("The delete operation is not supported for SQL views");
    }

    @Override
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("The update operation is not supported for SQL views");
    }

    @Override
    public int bulkInsert(SQLiteDatabase db, Uri uri, ContentValues[] values) {
        throw new UnsupportedOperationException("The bulk insert operation is not supported for SQL views");
    }

    // ====== HELPER METHODS

    protected Context getContext() {
        return ProvidersApplication.getContext();
    }

    protected ContentResolver getContentResolver() {
        return  getContext().getContentResolver();
    }
}
