package com.example.samo.aropen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.samo.aropen.Class.Artist;
import com.example.samo.aropen.Class.ArtistListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ListViewActivity extends BaseActivity {

    private static final String TAG = "ListActivity";
    private DatabaseReference mDatabase;
    private ArrayList<Artist> artistList = new ArrayList<>();
    Intent myIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list_view);
        Log.d(TAG, "onCreate: Started.");
        setTitle("Artists");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_list_view, null, false);
        mDrawerLayout.addView(contentView, 0);

        myIntent = new Intent(this, DetailsActivity.class);

        //Database Shit
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("artists");
        ArtistListAdapter adapter = new ArtistListAdapter(this, R.layout.adapter_view_layout, artistList);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "VALUE IS: " + dataSnapshot.getValue());
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Log.d(TAG, "KIND: " + child.getValue());
                    String artistId = child.getKey();


                    //child = einzelne artisten
                    //werden in einem Array (key, value prinzip) als obj gespeichert
                    Map<String, Object> obj =  (Map<String, Object>) child.getValue();
                    String name = (String) obj.get("name");
                    String firstName = (String) obj.get("alpha_name");
                    Artist artist = new Artist(artistId,firstName, name);
                    Log.i(TAG, "ARTIST IST: " + artist.getId() + "" + artist.getFirstname() + " "+ artist.getLastname());
                    artistList.add(artist);

                    Log.d(TAG, "ADAPTER: " +adapter);
                    ListView artistListView = (ListView) findViewById(R.id.listView);
                    artistListView.setAdapter(adapter);

                    artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView parent, View v, int position, long id) {
                            Log.d(TAG, "ES KLAPPT");

                            Artist list_row = artistList.get(position);
                            Intent detailActivity = new Intent(getApplicationContext(), DetailsActivity.class);
                            detailActivity.putExtra("firstname", list_row.getFirstname());
                            detailActivity.putExtra("id", list_row.getId());
                            //detailActivity.putExtra("", list_row.);

                            startActivity(detailActivity);
                        }

                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "DATABASE ERROR",databaseError.toException());
            }
        });

    }
}
