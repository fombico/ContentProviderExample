package com.example.providers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.providers.R;
import com.example.providers.tables.GenreTable;

public class GenreCursorAdapter extends CursorAdapter {

    public GenreCursorAdapter(Context context) {
        this(context, null);
    }

    public GenreCursorAdapter(Context context, Cursor c) {
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
        tv.setText(cursor.getString(cursor.getColumnIndex(GenreTable.Columns.GENRE_NAME)));
    }
}
