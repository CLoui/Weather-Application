/*
* Weather Application
* By: Courtney Loui
*
* This Application allows you to check the weather and climate conditions of the given coordinates.
* It gives you a weather recount of today, as well as the weather forecast for every three hours for the next five days
* */

//import packages
package com.example.weatherapp;

import android.Manifest;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherapp.Adapter.ViewPagerAdapter;
import com.example.weatherapp.Common.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

/*Main Activity*/
public class MainActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    private CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    // To be initiated when the app starts
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.root_view); // (CoordinatorLayout)
        Toolbar toolbar = findViewById(R.id.toolbar); //(Toolbar)
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Request Permission from user to use location
        Dexter.withActivity(this)
              .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(new MultiplePermissionsListener () {

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                      Snackbar.make(coordinatorLayout, "PERMISSION DENIED", Snackbar.LENGTH_LONG)
                              .show();
                  }

                  @Override
                  public void onPermissionsChecked (MultiplePermissionsReport report) {
                      if (report.areAllPermissionsGranted()) {
                          buildLocationRequest();
                          buildLocationCallback();

                          fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                          fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                      }
                  }

              }).check();

    }

    private void buildLocationRequest () {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    private void buildLocationCallback () {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult (LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.current_location = locationResult.getLastLocation();

                viewPager = findViewById(R.id.view_pager); // (ViewPager)
                setupViewPager(viewPager);
                tabLayout = findViewById(R.id.tabs); // (TabLayout)
                tabLayout.setupWithViewPager(viewPager);

                // Logs location
                Log.d("Location", locationResult.getLastLocation().getLatitude()+"/"+locationResult.getLastLocation().getLongitude());
            }
        };

    }

    private void setupViewPager (ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayWeatherFragment.getInstance(), "Today");
        adapter.addFragment(ForecastFragment.getInstance(), "5 Days");
        viewPager.setAdapter(adapter);

    }
}
