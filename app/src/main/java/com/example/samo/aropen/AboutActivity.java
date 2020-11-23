package com.example.samo.aropen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    private DatabaseReference mDatabase;
    Intent myIntent;
    private String contact;
    private String title;
    private String subtitle;
    private String message;
    TextView contactTextView;
    TextView titleTextView;
    TextView subtitleTextView;
    TextView messageTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
        Log.d(TAG, "onCreate: Started.");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setTitle("About");


        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_about, null, false);
        mDrawerLayout.addView(contentView, 0);

        //myIntent = new Intent(this, DetailsActivity.class);

        //Database Shit
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference aboutDatabase = mDatabase.child("about");

        aboutDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> obj = (Map<String, Object>) child.getValue();
                    contact = (String) obj.get("contact");
                    title = (String) obj.get("title");
                    subtitle = (String) obj.get("subtitle");
                    message = (String) obj.get("text");

                }
                contactTextView = (TextView) findViewById(R.id.aboutContact);
                titleTextView = (TextView) findViewById(R.id.aboutTitle);
                subtitleTextView = (TextView) findViewById(R.id.aboutSubtitle);
                messageTextView = (TextView) findViewById(R.id.aboutMessage);

                contactTextView.setText(contact);
                titleTextView.setText(title);
                subtitleTextView.setText(subtitle);
                messageTextView.setText(message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}