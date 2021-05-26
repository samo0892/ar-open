package com.example.samo.aropen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class ArCoreActivity extends BaseActivity {

    private static final String TAG = ArCoreActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private LocationScene locationScene;
    private ModelRenderable andyRenderable;
    ArSceneView arSceneView;
    private boolean installRequested;
    private boolean hasFinishedLoading = false;
    ArrayList<String> filePathList = new ArrayList<String>();
    ArrayList<File> localFiles = new ArrayList<File>();
    boolean userIsInCircle = false;
    private DatabaseReference mDatabase;
    String foundArtworkId;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        // Enable AR related functionality on ARCore supported devices only.
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_arcore, null, false);
        mDrawerLayout.addView(contentView, 0);
        arSceneView = findViewById(R.id.ar_scene_view);

        Intent intent = getIntent();

        foundArtworkId = intent.getStringExtra("foundArtworkId");

        if (foundArtworkId != null) {
            try {
                downloadObjects();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, Uri.parse("CHAHIN_EARTH.sfb"))
                .build();

        CompletableFuture.allOf(andy)
                .handle(
                        (notUsed, throwable) ->
                        {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }
                            try {
                                andyRenderable = andy.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }
                            return null;
                        });

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                locationScene = new LocationScene(this, this, arSceneView);

                                LocationMarker layoutLocationMarker = new LocationMarker(

                                        52.544583,
                                        13.354865,
                                        getModelObject()
                                );

                                // Adding the marker
                                locationScene.mLocationMarkers.add(layoutLocationMarker);
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }
                        });

        ARLocationPermissionHelper.requestPermission(this);
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }


    /***
     * Example Node of a 3D model
     *
     * @return
     */
    private Node getModelObject() {
        Node base = new Node();
        base.setRenderable(andyRenderable);
        return base;
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }
        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    private void downloadObjects() throws IOException {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference artworkDatabase = mDatabase.child("artworks");

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // Create a reference with an initial file path and name
        //StorageReference pathReference = storageRef.child("android/objects/CHAHIN_EARTH.mtl");

        artworkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> obj = (Map<String, Object>) child.getValue();
                    String foundArtwork = child.getKey();
                    if (foundArtwork.equals(foundArtworkId)) {
                        filename = (String) obj.get("titleDE");
                        if (!filePathList.contains((String) obj.get("bin_file"))) {
                            if (!((String) obj.get("bin_file")).isEmpty()) {
                                filePathList.add((String) obj.get("bin_file"));
                            }
                        }
                        if (!filePathList.contains((String) obj.get("model"))) {
                            if (!((String) obj.get("model")).isEmpty()) {
                                filePathList.add((String) obj.get("model"));
                            }
                        }
                        if (!filePathList.contains((String) obj.get("texture_file"))) {
                            if (!((String) obj.get("texture_file")).isEmpty()) {
                                filePathList.add((String) obj.get("texture_file"));
                            }
                        }
                    }
                }


                for (String filepath : filePathList) {
                    storageRef.child(filepath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String pathReference = uri.toString();
                            Log.d(TAG, "PAAATH: " + pathReference);
                        }
                    });

                    if (filepath.contains(".bin")) {
                        localFiles.add(new File(getCacheDir(), filename + ".bin"));
                    } else if (filepath.contains(".gltf")) {
                        localFiles.add(new File(getCacheDir(), filename + ".gltf"));
                    } else if (filepath.contains(".jpg")) {
                        localFiles.add(new File(getCacheDir(), filename + ".jpg"));
                    }

                    for (File localfile : localFiles) {
                        storageRef.child(filepath).getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                Log.d(TAG, "Local temp file has been created");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.e(TAG, exception.getMessage());
                            }
                        });
                    }
                }
                userIsInCircle = true;
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
