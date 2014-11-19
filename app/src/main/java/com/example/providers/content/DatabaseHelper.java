package com.example.providers.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.providers.Logger;
import com.example.providers.tables.ArtistTable;
import com.example.providers.tables.GenreTable;
import com.example.providers.tables.SongTable;
import com.example.providers.tables.Table;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "Music";
    static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static List<Table> mTables;
    static {
        mTables = new ArrayList<Table>();
        mTables.add(SongTable.getInstance());
        mTables.add(ArtistTable.getInstance());
        mTables.add(GenreTable.getInstance());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d("Database Helper onCreate");
        for (Table table : mTables) {
            table.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("Database Helper onUpgrade from " + oldVersion + " to " + newVersion);
        for (Table table : mTables) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
