package com.example.providers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.providers.R;
import com.example.providers.database.SampleSQLView;

public class SampleViewCursorAdapter extends CursorAdapter {

    public SampleViewCursorAdapter(Context context) {
        this(context, null);
    }

    public SampleViewCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.listview_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.listview_cell_text);

        String artist = cursor.getString(cursor.getColumnIndex(SampleSQLView.Columns.ARTIST_NAME));
        if (TextUtils.isEmpty(artist)) {
            artist = "";
        }

        String song = cursor.getString(cursor.getColumnIndex(SampleSQLView.Columns.SONG_NAME));
        if (TextUtils.isEmpty(song)) {
            song = "";
        }

        String genre = cursor.getString(cursor.getColumnIndex(SampleSQLView.Columns.GENRE_NAME));
        if (TextUtils.isEmpty(genre)) {
            genre = "";
        }


        tv.setText(artist + ", " + song + ", " + genre);
    }
}
