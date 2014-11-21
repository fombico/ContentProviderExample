package com.example.providers.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.providers.R;
import com.example.providers.adapters.SampleViewCursorAdapter;
import com.example.providers.database.SampleSQLView;

public class ViewSampleFragment extends CursorLoaderFragment {

    private SampleViewCursorAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new SampleViewCursorAdapter(getActivity());
        listView.setAdapter(mAdapter);
    }

    @Override
    public CursorAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public Uri getUri() {
        return SampleSQLView.URI;
    }
}
