package com.example.samo.aropen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.samo.aropen.Class.Site;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocation;
    private static final int Request_User_Location_Code = 99;
    private DatabaseReference mDatabase;
    private Circle circle;
    private float[] distance = new float[2];
    private String foundLat = "0";
    private String foundLong = "0";
    private String foundArtworkId;
    private String foundRadius = "0";
    ArrayList<File> localFiles = new ArrayList<File>();
    boolean userIsInCircle = false;
    ArrayList<Site> sites = new ArrayList<Site>();
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ArMap");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        i = new Intent(this, ArCoreActivity.class);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_maps, null, false);
        mDrawerLayout.addView(contentView, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bad_rabbit);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference sitesDatabase = mDatabase.child("sites");

            sitesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Map<Double, Object> obj = (Map<Double, Object>) child.getValue();
                        foundLat = (String) obj.get("latitude");
                        foundLong = (String) obj.get("longitude");
                        foundArtworkId = (String) obj.get("artwork");
                        foundRadius = (String) obj.get("radius");
                        String foundAddress = (String) obj.get("address");
                        Site s = new Site(foundLat, foundLong, foundArtworkId, foundRadius);
                        sites.add(s);

                        LatLng foundPlace = new LatLng(Double.parseDouble(foundLat), Double.parseDouble(foundLong));
                        mMap.addMarker(new MarkerOptions()
                                .position(foundPlace)
                                .title(foundAddress)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(Double.parseDouble(foundLat), Double.parseDouble(foundLong)))
                                .radius(Double.parseDouble(foundRadius))
                                .strokeWidth(3f)
                                .strokeColor(Color.RED)
                                .fillColor(Color.argb(70, 150, 50, 50))
                        );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT);
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentUserLocation != null) {
            Location.distanceBetween(currentUserLocation.getPosition().latitude,
                    currentUserLocation.getPosition().longitude, circle.getCenter().latitude,
                    circle.getCenter().longitude, distance);

            if (distance[0] > circle.getRadius()) {
                localFiles.removeAll(localFiles);
                userIsInCircle = false;

            } else if (distance[0] < circle.getRadius()) {
                if (!userIsInCircle) {
                    Intent arcoreActivity = new Intent(this, ArCoreActivity.class);
                    arcoreActivity.putExtra("foundArtworkId", foundArtworkId);

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("ArCamera")
                            .setMessage("Soll die ArCamera gestartet werden?")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .show();

                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(arcoreActivity);
                        }
                    });
                    userIsInCircle = true;
                }
            }
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        currentUserLocation = mMap.addMarker(markerOptions);
        currentUserLocation.setVisible(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        if (lastLocation == null) {
            mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
            lastLocation = location;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            deleteTempFiles(getCacheDir());
        }
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }
}
