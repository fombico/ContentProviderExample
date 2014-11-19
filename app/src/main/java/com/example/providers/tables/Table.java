package com.example.providers.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.providers.ProvidersApplication;
import com.example.providers.content.UriRouter;

public abstract class Table implements UriRouter.UriRoute {

    public static class Columns {
        public static final String _ID = "_id";
    }

    public abstract String getTableName();

    public abstract String getCreateSQL();

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateSQL());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(db);
    }

    protected Context getContext() {
        return ProvidersApplication.getContext();
    }

    protected ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

    @Override
    public String getType(Uri uri) {
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
}
