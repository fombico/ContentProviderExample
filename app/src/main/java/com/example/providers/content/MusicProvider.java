package com.example.providers.content;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.providers.Logger;
import com.example.providers.tables.ArtistTable;
import com.example.providers.tables.GenreTable;
import com.example.providers.tables.SongTable;

import java.util.ArrayList;

public class MusicProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.provider.music";
    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        Logger.d("ContentProvider onCreate");
        mDatabaseHelper = new DatabaseHelper(getContext());
        initUriRouter();
        return true;
    }

    private void initUriRouter() {
        UriRouter router = UriRouter.getInstance();
        router.addRoute(ArtistTable.getInstance());
        router.addRoute(GenreTable.getInstance());
        router.addRoute(SongTable.getInstance());
        Logger.d("added uri routes");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Logger.d("Querying provider with uri: " + uri);
        return UriRouter.getInstance().query(getDB(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        Logger.d("Get type in provider with uri: " + uri);
        return UriRouter.getInstance().getType(uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Logger.d("Inserting into provider with uri: " + uri);
        return UriRouter.getInstance().insert(getDB(), uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Logger.d("Deleting from provider with uri: " + uri);
        return UriRouter.getInstance().delete(getDB(), uri, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logger.d("Updating provider with uri: " + uri);
        return UriRouter.getInstance().update(getDB(), uri, values, selection, selectionArgs);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Logger.d("Bulk Insert into provider with uri: " + uri);
        return UriRouter.getInstance().bulkInsert(getDB(), uri, values);
    }

    private SQLiteDatabase getDB() {
        return mDatabaseHelper.getWritableDatabase();
    }

}