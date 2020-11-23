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


public class DetailsActivity extends BaseActivity {

    private static final String TAG = "DetailActivity";
    private DatabaseReference mDatabase;
    ListView myListView;
    private ArrayList<ArtworkDescription> artworkList = new ArrayList<>();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    final long ONE_MEGABYTE = 1024 * 1024;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_artist_detail);
        Log.d(TAG, "onCreate: Started.");
        setTitle("Details of Artist");


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_artist_detail, null, false);
        mDrawerLayout.addView(contentView, 0);

        //the stuff to show all the gotten artwork_description in a layout
        ArtworkDescriptionListAdapter adapter = new ArtworkDescriptionListAdapter(this, artworkList);
        myListView = (ListView) findViewById(R.id.descriptionListView);

        //get the intent(value) from ListViewActivity
        Intent secondIntent = getIntent();
        String artistId = secondIntent.getStringExtra("id");


        //connect to the database and to the table "artwork_description"
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference artDescriptionDatabase = mDatabase.child("artwork_description");

        artDescriptionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Artworks are: " + dataSnapshot.getValue());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> obj = (Map<String, Object>) child.getValue();
                    String foundId = (String) obj.get("artist_id");
                    if (foundId.equals(artistId)) {
                        Log.d(TAG, "ID WURDE GEFUNDEN!");

                        String description = (String) obj.get("descriptionDE");
                        String title = (String) obj.get("titleDE");
                        String imageUrl = (String) obj.get("image_url");
                        Log.d(TAG, "IMAGE URL: " + imageUrl);
                        storage.child(imageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String pathReference = uri.toString();
                                Log.d(TAG, "PAAATH: " + pathReference);
                                ArtworkDescription artworkDescription = new ArtworkDescription(description, title, pathReference);
                                artworkList.add(artworkDescription);
                                myListView.setAdapter(adapter);
                            }
                        });

                        Log.d(TAG, " IMAGE_URL: " + imageUrl);
                        //ArtworkDescription artworkDescription = new ArtworkDescription(description, title, pathReference);

                        //artworkList.add(artworkDescription);
                        myListView.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "ID WURDE NICHT GEFUNDEN!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

    }
}
