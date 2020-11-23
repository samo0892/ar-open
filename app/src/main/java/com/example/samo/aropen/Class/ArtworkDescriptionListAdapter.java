package com.example.samo.aropen.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samo.aropen.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtworkDescriptionListAdapter extends BaseAdapter {

    private ArrayList<ArtworkDescription> singleArtwork; //get the items of class ArtworkDescription in an array
    private LayoutInflater thisInflater; //will be used to create a row from that XML file

    public ArtworkDescriptionListAdapter(Context context, ArrayList<ArtworkDescription> aRow) {
        this.singleArtwork = aRow;
        thisInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return singleArtwork.size();
    }

    @Override
    public Object getItem(int position) {

        return singleArtwork.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    //convertView hold all the rows for the ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = thisInflater.inflate(R.layout.description_list_view_layout, parent, false);
            TextView theTitle = (TextView) convertView.findViewById(R.id.artworkTitle);
            TextView theDescription = (TextView) convertView.findViewById(R.id.artworkDescription);
            ImageView theImage = (ImageView) convertView.findViewById(R.id.artworkImage);

            ArtworkDescription currentArtworkDescription = (ArtworkDescription) getItem(position);
            String imgPath = currentArtworkDescription.getImageUrl();

            theTitle.setText(currentArtworkDescription.getTitle());
            theDescription.setText(currentArtworkDescription.getDescription());
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(imgPath).into(theImage);

        }

        return convertView;
    }
}
