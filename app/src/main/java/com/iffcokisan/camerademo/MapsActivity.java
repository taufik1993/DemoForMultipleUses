package com.iffcokisan.camerademo;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(28.5494748, 77.251048);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Nehru Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(28.54942, 77.2510623), new LatLng(28.5494748, 77.251048), new LatLng(28.5496997, 77.2509465), new LatLng(28.5497277, 77.2513718)
                        , new LatLng(28.5490606, 77.2516766), new LatLng(28.5480088, 77.2524173), new LatLng(28.5480171, 77.2512313), new LatLng(28.5483863, 77.250696))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

    }
}
