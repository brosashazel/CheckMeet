package com.example.checkmeet.view;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Meeting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private String address;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get latitude and longitude and address
        latitude = getIntent().getDoubleExtra(Meeting.COL_LATITUDE, -1);
        longitude = getIntent().getDoubleExtra(Meeting.COL_LONGITUDE, -1);
        address = getIntent().getStringExtra(Meeting.COL_ADDRESS);
        title = getIntent().getStringExtra(Meeting.COL_TITLE);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng addressLatLng = new LatLng(latitude, longitude);

        // Add a marker in specified location
        googleMap.addMarker(new MarkerOptions()
                .position(addressLatLng)
                .title(title)
                .snippet(address)
        );

        // move camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

    }
}
