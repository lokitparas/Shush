package com.example.lokit.shush;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.DocumentsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int seekBarValue;
    private Double currLat;
    private Double currLong;
    private Circle c;
    private SeekBar s;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        s = (SeekBar) findViewById(R.id.seekBar);
        done = (Button) findViewById(R.id.done);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, Integer.toString(seekBarValue),Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                data.putExtra("CurrLong", String.valueOf(currLong));
                data.putExtra("CurrLat", String.valueOf(currLat));
                data.putExtra("radius", String.valueOf(seekBarValue*10));
                data.putExtra("Long", String.valueOf(c.getCenter().longitude));
                data.putExtra("Lat", String.valueOf(c.getCenter().latitude));
                setResult(RESULT_OK, data);

                finish();

            }
        });

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

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        currLat = bestLocation.getLatitude();
        currLong = bestLocation.getLongitude();
        return bestLocation;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


//And the all the codes I had before
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions( MapsActivity.this, "PERMISSIONS_LOCATION", REQUEST_LOCATION);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Criteria criteria = new Criteria();
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = getLastKnownLocation();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng latLng = new LatLng(lat, lng);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            final Marker perth = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker").draggable(true).snippet("Snippet"));
            c = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(perth.getPosition().latitude, perth.getPosition().longitude))
                    .radius(0)
                    .strokeColor(Color.BLUE)
                    );
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng arg0) {
                    // TODO Auto-generated method stub
                    Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
//                Toast.makeText(
//                        MapsActivity.this,
//                        "Lat " + arg0.latitude + " "
//                                + "Long " + arg0.longitude,
//                        Toast.LENGTH_LONG).show();
                    perth.setPosition(arg0);
                    c.setCenter(arg0);

                }
            });

            s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // TODO Auto-generated method stub

                    seekBarValue = 10*progress;
                    c.setRadius(seekBarValue);
                    // Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                LatLng position = marker.getPosition();
//
////                Toast.makeText(
////                        MapsActivity.this,
////                        "Lat " + position.latitude + " "
////                                + "Long " + position.longitude,
////                        Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng arg0) {
//                // TODO Auto-generated method stub
//                Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
////                Toast.makeText(
////                        MapsActivity.this,
////                        "Lat " + arg0.latitude + " "
////                                + "Long " + arg0.longitude,
////                        Toast.LENGTH_LONG).show();
//                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.latitude, arg0.longitude)).title("Marker").snippet("Snippet"));
//            }
//        });

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == MY_LOCATION_REQUEST_CODE) {
//            if (permissions.length == 1 &&
//                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mMap.setMyLocationEnabled(true);
//            } else {
//                // Permission was denied. Display an error message.
//            }
//        }
//    }
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        // Create a criteria object to retrieve provider
//                 Criteria criteria = new Criteria();
//
//                 // Get the name of the best provider
//                 String provider = locationManager.getBestProvider(criteria, true);
//
//                 // Get Current Location
//                 Location myLocation = locationManager.getLastKnownLocation(provider);
//        double latitude;
//        double longitude;
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        if (myLocation != null) {
//            latitude = myLocation.getLatitude();
//            longitude = myLocation.getLongitude();
//            LatLng latLng = new LatLng(latitude, longitude);
//
//                 // Show the current location in Google Map
//                 mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map


        // set map type


        // Get latitude of the current location
//                 double latitude = myLocation.getLatitude();
//
//                 // Get longitude of the current location
//                 double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
//                 LatLng latLng = new LatLng(latitude, longitude);
//
//                 // Show the current location in Google Map
//                 mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                 // Zoom in the Google Map
//                 mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//                 mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
};
