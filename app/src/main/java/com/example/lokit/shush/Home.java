package com.example.lokit.shush;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import  java.lang.Math;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {
    public static final String TABLE_RECORDS = "records";
    private static final int NEW_ADDED = 10;
    protected MyApplication app;
    protected MySQLiteHelper mDB;
    protected ListView items;
    private GoogleMap mMap;
    private Double currLat;
    private Double currLong;
    public TextView lattext;
    public TextView longtext;
    public ListView activeList;
    public RelativeLayout data;
    AudioManager audio;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public AlertDialog alertDialog;


    public TextView locText1;
    public TextView locText2;
    public TextView timeText;
    public TextView mon;
    public TextView tue;
    public TextView wed;
    public TextView thu;
    public TextView fri;
    public TextView sat;
    public TextView sun;
    public ImageButton delete;
    public RelativeLayout overlay;
    public String currentMarker;
    public TextView Heading;

    ArrayList<Record> recordList = new ArrayList<Record>();

    String getProviderName() {
    LocationManager locationManager = (LocationManager) this
            .getSystemService(Context.LOCATION_SERVICE);

    Criteria criteria = new Criteria();
    criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
    criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
    criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
    criteria.setAltitudeRequired(false); // Choose if you use altitude.
    criteria.setBearingRequired(false); // Choose if you use bearing.
    criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

    // Provide your criteria and flag enabledOnly that tells
    // LocationManager only to return active providers.
    return locationManager.getBestProvider(criteria, true);
}

    void showRecords(){
        for(Record g: recordList){
            g.getename();
            MarkerOptions m = new MarkerOptions();
            m.position(new LatLng(g.getLocation()[0], g.getLocation()[1]));
            m.title(g.getename());
            String temp = g.getstime()+";"+g.getetime()+";";
            for(int i: g.getWeekdays()){
                temp+= Integer.toString(i)+";";
            }
            m.snippet(temp);
            Marker newmarker = mMap.addMarker(m);

        }


    }

    void checkbounds(Location l){
        Log.i("Home","Inside checker without service");
        boolean inside = false;
        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        for (Record g: recordList){
            if(g.getWeekdays()[d-1] == 1) {

                String[] splitStringEnd = g.getetime().split(":",2);
                String[] splitStringStart = g.getstime().split(":",2);


                if (hour <= Integer.parseInt(splitStringEnd[0]) && hour >= Integer.parseInt(splitStringStart[0])) {
                    if (minute <= Integer.parseInt(splitStringEnd[1]) && minute >= Integer.parseInt(splitStringStart[1])) {
                        final double RADIUS = 6371.01;
                        double temp = Math.cos(Math.toRadians(g.getLocation()[0])) * Math.cos(Math.toRadians(l.getLatitude())) * Math.cos(Math.toRadians((g.getLocation()[1]) - (l.getLongitude())))
                                + Math.sin(Math.toRadians(g.getLocation()[0])) * Math.sin(Math.toRadians(l.getLatitude()));
                        double dist = temp * RADIUS * Math.PI / 180;
                        //double dist = Math.sqrt((g.getLocation()[0]-l.getLatitude())*(g.getLocation()[0]-l.getLatitude()) + (g.getLocation()[1] - l.getLongitude())*(g.getLocation()[1] - l.getLongitude()));
                        Log.e("Home-dist",Double.toString(dist));
                        Log.e("Home-radius",Double.toString(g.getLocation()[2]));
                        if (dist < g.getLocation()[2]) {
                            Log.e("HOME-dist","inhere");
                            audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            inside = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!inside){
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }

//    void checkbounds(Location l){
//        Log.i("Home","Inside checker without service");
//        boolean inside = false;
//        for (Record g: recordList){
//            final double RADIUS = 6371.01;
//            double temp = Math.cos(Math.toRadians(g.getLocation()[0])) * Math.cos(Math.toRadians(l.getLatitude())) * Math.cos(Math.toRadians((g.getLocation()[1]) - (l.getLongitude())))
//                    + Math.sin(Math.toRadians(g.getLocation()[0])) * Math.sin(Math.toRadians(l.getLatitude()));
//            double dist = temp * RADIUS * Math.PI / 180;
//            //double dist = Math.sqrt((g.getLocation()[0]-l.getLatitude())*(g.getLocation()[0]-l.getLatitude()) + (g.getLocation()[1] - l.getLongitude())*(g.getLocation()[1] - l.getLongitude()));
//            if(dist < g.getLocation()[2]){
//                audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//                inside =true;
//                break;
//            }
//        }
//        if(!inside){
//            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//        }
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        app = (MyApplication) getApplication();
        mDB = new MySQLiteHelper(this);


        Bundle bun = getIntent().getExtras();
        //final String eventtype = bun.getString("eventtype").toString();
        if(bun != null) {
            final ArrayList<Record> curr_list = bun.getParcelableArrayList("NEW_LIST");
            if (curr_list != null) {


                mDB.addRecord(curr_list.get(curr_list.size() - 1));
                recordList = mDB.getAllRecords();
                Toast.makeText(this, Integer.toString(recordList.size()),
                        Toast.LENGTH_LONG).show();
            }

        }
        else{
            recordList = mDB.getAllRecords();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        data = (RelativeLayout) findViewById(R.id.data);
        audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        locText1 = (TextView) findViewById(R.id.Loctext1);
        locText2 = (TextView) findViewById(R.id.Loctext2);
        timeText = (TextView) findViewById(R.id.Timetext);
        mon = (TextView)findViewById(R.id.mon);
        tue = (TextView)findViewById(R.id.tue);
        wed = (TextView)findViewById(R.id.wed);
        thu = (TextView)findViewById(R.id.thu);
        fri = (TextView)findViewById(R.id.fri);
        sat = (TextView)findViewById(R.id.sat);
        sun = (TextView)findViewById(R.id.sun);
        delete = (ImageButton)findViewById(R.id.delete);
        overlay = (RelativeLayout)findViewById(R.id.overlay);
        Heading = (TextView) findViewById(R.id.Head);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(getProviderName(), 5*1000, (float) 1, this);

        if(recordList.size() != 0){
            Intent intent = new Intent(this, SilenceService.class);
            intent.putExtra("RECORD_LIST",recordList);
            intent.putExtra("CURR_LAT",currLat);
            intent.putExtra("CURR_LONG",currLong);
            startService(intent);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent = new Intent(Home.this,fillLoc.class);
                mainIntent.putParcelableArrayListExtra("CURR_LIST",recordList);
                Home.this.startActivity(mainIntent);
                finish();
//                if(currLat != null && currLat != null) {
//                    System.out.print(currLat);
//                    System.out.print(currLat);
//
//                    lattext.setText(String.valueOf(currLat));
//                    longtext.setText(String.valueOf(currLong));
//                }
                //sendMessage(view);
            }
        });

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("HOME","Inside Delete");
                Log.e("HOME","CurrentMarker"+currentMarker);
                Log.e("HOME",Integer.toString(recordList.size()));

                for(int i=0; i<recordList.size();i++){
                    if(recordList.get(i).getename().equals(currentMarker)){
                        Log.e("HOME","found it");
                        mDB.removeRecord(recordList.get(i));
                        recordList.remove(i);
                        mMap.clear();
                        showRecords();
                        break;
                    }
                }
                overlay.setVisibility(View.GONE);
                data.setVisibility(View.GONE);

            }
        });
        //showRecords();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 10) {
//            Bundle bun = intent.getExtras();
//            Record newrecord = (Record) bun.getParcelable("NEW_RECORD");
//            recordList.add(newrecord);
            Toast.makeText(this, "this is my Toast message!!! =)",
                    Toast.LENGTH_LONG).show();
            //showRecords(recordList); // your "refresh" code
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendMessage(View view){
        Intent intent = new Intent(Home.this, addTask.class);
        Home.this.startActivity(intent);
        Home.this.finish();
    }


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
//        lattext.setText(currLat.toString());
//        longtext.setText(currLong.toString());
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                data.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
                String timestring = marker.getSnippet();
                String[] splitString = timestring.split(";");
                String result = String.format("%.4f",marker.getPosition().latitude);
                String result1 = String.format("%.4f", marker.getPosition().longitude);
                locText1.setText("Lat: " +result);
                locText2.setText(" Long: "+result1);
                Heading.setText(marker.getTitle());
                timeText.setText(splitString[0] +" to "+splitString[1]);
                if(Integer.parseInt(splitString[2]) == 1){mon.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[3]) == 1){tue.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[4]) == 1){wed.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[5]) == 1){thu.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[6]) == 1){fri.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[7]) == 1){sat.setTextColor(getResources().getColor(R.color.darkgrey));}
                if(Integer.parseInt(splitString[8]) == 1){sun.setTextColor(getResources().getColor(R.color.darkgrey));}
                currentMarker = marker.getTitle();
                return true;
            }
        });
        mMap.setMyLocationEnabled(true);
        //mMap.setOnMyLocationButtonClickListener(this);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng latLng = new LatLng(lat, lng);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            //final Marker perth = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker").draggable(true).snippet("Snippet"));

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
        showRecords();
    }

    @Override
    public void onLocationChanged(Location location) {
        //lattext.setText(String.valueOf(location.getLatitude()));
        //longtext.setText(String.valueOf(location.getLongitude()));
        checkbounds(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
