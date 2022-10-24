package com.example.mymapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    Button btnGetLocation;
    LocationManager locationManager;
    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    boolean permissionsGranted = false;
    boolean locationEnabled = false;
    View btnMapLocation;  // Button within map fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetLocation = findViewById(R.id.btnGetLocation);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);
        getPermissions();
    }

    private boolean getPermissions() {
       //Check permissions variable first
       if (permissionsGranted) {return true; }

        // Check if fine location permission has been granted, otherwise request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        permissionsGranted = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsGranted) { return;}  // No need to continue

        // Check if location permissions have been requested
        int index = Arrays.asList(permissions).indexOf(Manifest.permission.ACCESS_FINE_LOCATION);
        if (index == -1) { return;}

        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
            // Location permission granted
            permissionsGranted = true;
            startMyLocationLayer(googleMap);
        } else {
            // location permission denied
            Toast.makeText(getApplicationContext(),"Precise location permissions are needed in order to make use of this application.", Toast.LENGTH_LONG).show();
        }
    }

    public void btnGetLocationClick(View view) {
        if (permissionsGranted) {
            if (locationEnabled) {
                btnMapLocation.performClick();
            } else {
                startMyLocationLayer(googleMap);
            }
        } else {
            getPermissions();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        startMyLocationLayer(googleMap);
    }

    private void startMyLocationLayer(GoogleMap googleMap) {
        if (permissionsGranted) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setOnMyLocationClickListener(this);
            btnMapLocation = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            btnMapLocation.setVisibility(View.INVISIBLE);
            locationEnabled = true;
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setOnMyLocationClickListener(this);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
       return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        return;
    }
}