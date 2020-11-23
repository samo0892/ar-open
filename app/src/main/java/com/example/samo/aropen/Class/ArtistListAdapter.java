package com.example.samo.aropen.Class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.samo.aropen.R;

import java.util.ArrayList;

public class ArtistListAdapter extends ArrayAdapter {

    private static final String TAG = "ArtistListAdapter";

    private Context mContext;
    int mResources;
    private ArrayList<Artist> singleArtist;

    public ArtistListAdapter(Context context, int resource, ArrayList<Artist> objects) {
        super(context, resource, objects);
        mContext = context;
        mResources = resource;
        this.singleArtist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String id = getItem(position).getId();
        String name = getItem(position).getFirstname();
        String lastname = getItem(position).getLastname();

        Artist artist = new Artist(id, name, lastname);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResources, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textView2);

        tvName.setText(lastname);

        return convertView;

    }

    public int getCount() {
        return singleArtist.size();
    }

    public Artist getItem(int position) {
        return singleArtist.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
}
