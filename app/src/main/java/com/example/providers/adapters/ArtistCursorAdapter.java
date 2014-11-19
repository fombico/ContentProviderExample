package com.example.providers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.providers.R;
import com.example.providers.tables.ArtistTable;

public class ArtistCursorAdapter extends CursorAdapter {

    public ArtistCursorAdapter(Context context) {
        this(context, null);
    }

    public ArtistCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // TODO ViewHolders
        return inflater.inflate(R.layout.listview_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.listview_cell_text);
        tv.setText(cursor.getString(cursor.getColumnIndex(ArtistTable.Columns.ARTIST_NAME)));
    }
}
