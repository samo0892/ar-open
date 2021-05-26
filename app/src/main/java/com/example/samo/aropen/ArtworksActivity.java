package com.example.samo.aropen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.example.samo.aropen.Class.ArtworkDescription;
import com.example.samo.aropen.Class.ArtworkDescriptionListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class ArtworksActivity extends BaseActivity {

    private static final String TAG = "ArtworksActivity";
    private DatabaseReference mDatabase;
    private ArrayList<ArtworkDescription> artworkList = new ArrayList<>();
    Intent myIntent;
    ListView myListView;
    StorageReference storage = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Artworks");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_artist_detail, null, false);
        mDrawerLayout.addView(contentView, 0);

        //the stuff to show all the gotten artwork_description in a layout
        ArtworkDescriptionListAdapter adapter = new ArtworkDescriptionListAdapter(this, artworkList);
        myListView = (ListView) findViewById(R.id.descriptionListView);

        //connect to the database and to the table "artwork_description"
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference artDescriptionDatabase = mDatabase.child("artwork_description");

        artDescriptionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Artworks are: " + dataSnapshot.getValue());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> obj = (Map<String, Object>) child.getValue();

                    String description = (String) obj.get("descriptionDE");
                    String title = (String) obj.get("titleDE");
                    String imageUrl = (String) obj.get("image_url");
                    storage.child(imageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String pathReference = uri.toString();
                            ArtworkDescription artworkDescription = new ArtworkDescription(description, title, pathReference);
                            artworkList.add(artworkDescription);
                            myListView.setAdapter(adapter);
                        }
                    });
                    myListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
