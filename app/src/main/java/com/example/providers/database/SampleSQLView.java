package com.example.providers.database;

import android.net.Uri;

import com.example.providers.content.MusicProvider;

import java.util.ArrayList;
import java.util.List;

public class SampleSQLView extends SQLView {

    public static String VIEW_NAME = "sample";
    public static final Uri URI = Uri.parse("content://" + MusicProvider.AUTHORITY + "/" + VIEW_NAME);
    private static SampleSQLView sView;

    private SampleSQLView() {

    }

    public static SampleSQLView getInstance() {
        if (sView == null) {
            sView = new SampleSQLView();
        }
        return sView;
    }

    public static class Columns extends SQLView.Columns {
        public static final String ARTIST_NAME = ArtistTable.Columns.ARTIST_NAME;
        public static final String GENRE_NAME = GenreTable.Columns.GENRE_NAME;
        public static final String SONG_NAME = SongTable.Columns.SONG_NAME;
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public Uri getUri() {
        return URI;
    }

    @Override
    public String getSelectStatement() {
        return
            "SELECT " +
                "tmp." + Columns._ID + " AS " + Columns._ID + ", " +
                "tmp." + Columns.SONG_NAME + " AS " + Columns.SONG_NAME + ", " +
                "tmp." + Columns.ARTIST_NAME + " AS " + Columns.ARTIST_NAME + ", " +
                GenreTable.Columns.GENRE_NAME + " AS " + Columns.GENRE_NAME + " " +
            "FROM " +
                "(" +
                    "SELECT " + SongTable.TABLE_NAME + "." + SongTable.Columns._ID + " AS " + SQLView.Columns._ID + ", " +
                    SongTable.TABLE_NAME + "." + SongTable.Columns.SONG_NAME + ", " +
                    ArtistTable.TABLE_NAME + "." + ArtistTable.Columns.ARTIST_NAME +
                    " FROM " +
                    SongTable.TABLE_NAME + " LEFT OUTER JOIN " + ArtistTable.TABLE_NAME +
                    " ON " + SongTable.TABLE_NAME + "." + SongTable.Columns._ID + " = " + ArtistTable.TABLE_NAME + "." + ArtistTable.Columns._ID +
                ") tmp " +
            "LEFT OUTER JOIN " + GenreTable.TABLE_NAME + " ON tmp." + Columns._ID + " = " + GenreTable.TABLE_NAME + "." + GenreTable.Columns._ID;
    }

    @Override
    protected List<Uri> getUrisToObserve() {
        List<Uri> list = new ArrayList<Uri>();
        list.add(ArtistTable.URI);
        list.add(GenreTable.URI);
        list.add(SongTable.URI);
        return list;
    }
}
