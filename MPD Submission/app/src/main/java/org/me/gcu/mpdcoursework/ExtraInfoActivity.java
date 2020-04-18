package org.me.gcu.mpdcoursework;
//Ben Ivory S1621251

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExtraInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private TrafficAccident trafficAccident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_info);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        trafficAccident = (TrafficAccident) intent.getSerializableExtra("TrafficAccident");

        TextView textTitle = findViewById(R.id.textTitle);
        TextView textDescription = findViewById(R.id.textDescription);

        textTitle.setText(trafficAccident.title);
        textDescription.setText(Html.fromHtml(trafficAccident.description));
    }

    public void onMapReady(GoogleMap gMap) {
        this.googleMap = gMap;

        //Get lat lng from trafficAccident!
        LatLng latLng = new LatLng(trafficAccident.latitude,trafficAccident.longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(trafficAccident.title));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
    }
}
