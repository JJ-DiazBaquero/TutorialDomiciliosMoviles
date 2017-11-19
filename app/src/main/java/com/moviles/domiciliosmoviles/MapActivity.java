package com.moviles.domiciliosmoviles;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        findViewById(R.id.select_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent()
                        .putExtra("latitude",latitude)
                        .putExtra("longitude",longitude);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    private double latitude;
    private double longitude;

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;

        }
        else {
            //Get Last Known Location and convert into Current Longitute and Latitude
            final LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            final Location currentGeoLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double currentLat = currentGeoLocation.getLatitude();
            double currentLon = currentGeoLocation.getLongitude();
            LatLng currentLatLng = new LatLng(currentLat,currentLon);

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));

            this.map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    //get latlng at the center by calling
                    LatLng midLatLng = map.getCameraPosition().target;
                    latitude = midLatLng.latitude;
                    longitude = midLatLng.longitude;
                }
            });

            mostrarDomiciliarios();
        }

    }

    private void mostrarDomiciliarios(){

        ArrayList<LatLng> puntos = new ArrayList<LatLng>();
        puntos.add(new LatLng(4.604942, -74.065975));
        puntos.add(new LatLng(4.604124, -74.066169));
        puntos.add(new LatLng(4.603386, -74.065697));
        puntos.add(new LatLng(4.602071, -74.064452));
        puntos.add(new LatLng(4.602114, -74.067456));
        puntos.add(new LatLng(4.602114, -74.067456));

        for (LatLng punto : puntos) {
            map.addMarker(new MarkerOptions()
                    .position(punto)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2)));
        }

    }

}
