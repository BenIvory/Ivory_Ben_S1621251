package org.me.gcu.mpdcoursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

//The LOCATION code was adapted from this website.
//https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html
//Ferdousur Rahman Sarker
//November 18, 2019
//2020 AndroDocs.com

public class PlotActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    public static List<TrafficAccident> trafficAccidents = new ArrayList<>();
    private Dictionary<Marker, TrafficAccident> markerTrafficAccidentPairs = new Hashtable<>();
    private  String clickedMarker = "";

    //Location
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setOnMarkerClickListener(this);

        //Plot markers
        for (TrafficAccident ta : trafficAccidents) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(ta.title);
            markerOptions.position(new LatLng(ta.latitude, ta.longitude));

            Marker marker = googleMap.addMarker(markerOptions);
            markerTrafficAccidentPairs.put(marker, ta);
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (clickedMarker.equals(marker.getId())) {
            Intent intent = new Intent(this, ExtraInfoActivity.class);
            intent.putExtra("TrafficAccident", markerTrafficAccidentPairs.get(marker));
            startActivity(intent);

            //Override default behaviour.
            return true;
        } else {
            clickedMarker = marker.getId();
            //Continue with default behaviour.
            return false;
        }
    }


    //Start of LOCATION
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                requestNewLocationData();
                                float lat = Double.valueOf(location.getLatitude()).floatValue();
                                float lng = Double.valueOf(location.getLongitude()).floatValue();
                                MoveMap(lat, lng);
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            float lat = Double.valueOf(mLastLocation.getLatitude()).floatValue();
            float lng = Double.valueOf(mLastLocation.getLongitude()).floatValue();
            MoveMap(lat, lng);
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    private void MoveMap(float lat, float lng) {
        LatLng latLng = new LatLng(lat, lng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
    }
    //End of LOCATION
}
