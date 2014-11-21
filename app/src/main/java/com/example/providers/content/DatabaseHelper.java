package com.example.providers.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.providers.Logger;
import com.example.providers.database.ArtistTable;
import com.example.providers.database.GenreTable;
import com.example.providers.database.SQLTable;
import com.example.providers.database.SQLView;
import com.example.providers.database.SampleSQLView;
import com.example.providers.database.SongTable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "Music";
    static final int DATABASE_VERSION = 1;

    static List<SQLTable> mTables;
    static {
        mTables = new ArrayList<SQLTable>();
        mTables.add(SongTable.getInstance());
        mTables.add(ArtistTable.getInstance());
        mTables.add(GenreTable.getInstance());
    }

    static List<SQLView> mViews;
    static {
        mViews = new ArrayList<SQLView>();
        mViews.add(SampleSQLView.getInstance());
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d("Database Helper onCreate");
        db.beginTransaction();
        for (SQLTable table : mTables) {
            table.onCreate(db);
        }

        Logger.d("Database Helper onCreate - tables complete");
        for (SQLView view : mViews) {
            view.onCreate(db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("Database Helper onUpgrade from " + oldVersion + " to " + newVersion);
        for (SQLTable table : mTables) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        for (SQLView view : mViews) {
            // If we call this too early, the Application Context it uses may be null.
            // Also make sure the database isn't opened in the ContentProvider.onCreate(),
            // otherwise the context would be null too.
            view.initContentObserver();
        }
        super.onOpen(db);
    }
}
