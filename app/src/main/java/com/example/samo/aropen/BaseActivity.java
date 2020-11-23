package com.example.samo.aropen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mToggle;
    protected NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_base);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.exhibition:
                        Intent launchNewIntent = new Intent(BaseActivity.this, MapsActivity.class);
                        startActivityForResult(launchNewIntent, 0);
                        return true;

                    case R.id.artists:
                        Intent launchNewIntent1 = new Intent(BaseActivity.this, ListViewActivity.class);
                        startActivityForResult(launchNewIntent1, 0);
                        return true;

                    case R.id.about:
                        Intent launchNewIntent2 = new Intent(BaseActivity.this, AboutActivity.class);
                        startActivityForResult(launchNewIntent2, 0);
                        return true;

                    case R.id.arcamera:
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent launchNewIntent3 = new Intent(BaseActivity.this, ArCoreActivity.class);
                        startActivityForResult(launchNewIntent3, 0);
                        return true;

                    case R.id.artworks:
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent launchNewIntent4 = new Intent(BaseActivity.this, ArtworksActivity.class);
                        startActivityForResult(launchNewIntent4, 0);
                        return true;

                    default:
                        return true;
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mDrawerLayout.openDrawer(nv);


        if (mToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "IS CLICKED: " + item.getItemId());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
