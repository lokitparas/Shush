package com.example.lokit.shush;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

public class SilenceService extends Service implements LocationListener {

    private static final String TAG = "HelloService";
    public ArrayList<Record> recordList = new ArrayList<Record>();
    public LatLng Currentlocation;
    public boolean locChanged=false;
    private boolean isRunning = false;
    public AudioManager audio;
    protected MySQLiteHelper mDB;

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

    void checkbounds(LatLng l){
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
//                Log.e("HOME-starttime",g.getstime());
//                Log.e("HOME-endtime",g.getetime());
//                Log.e("HOME-currtime",Integer.toString(hour)+":"+Integer.toString(minute));

                if (hour <= Integer.parseInt(splitStringEnd[0]) && hour >= Integer.parseInt(splitStringStart[0])) {
                    if (minute <= Integer.parseInt(splitStringEnd[1]) && minute >= Integer.parseInt(splitStringStart[1])) {
                        final double RADIUS = 6371.01;
                        double temp = Math.cos(Math.toRadians(g.getLocation()[0])) * Math.cos(Math.toRadians(l.latitude)) * Math.cos(Math.toRadians((g.getLocation()[1]) - (l.longitude)))
                                + Math.sin(Math.toRadians(g.getLocation()[0])) * Math.sin(Math.toRadians(l.latitude));
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
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        mDB = new MySQLiteHelper(this);
        audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        if(intent != null) {
            Bundle bun = intent.getExtras();
            //final String eventtype = bun.getString("eventtype").toString();
            if (bun != null) {
                final ArrayList<Record> curr_list = bun.getParcelableArrayList("RECORD_LIST");
                if (curr_list != null) {
                    recordList = curr_list;

                }

                Log.i(TAG, Integer.toString(recordList.size()));
                double currentlat = bun.getDouble("CURR_LAT");
                double currentlong = bun.getDouble("CURR_LONG");
                Currentlocation = new LatLng(currentlat, currentlong);
            }
        }
        else{
            recordList = mDB.getAllRecords();
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        locationManager.requestLocationUpdates(getProviderName(), 5*1000, 1, this);
        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                while(recordList.size()!= 0){
                    if(locChanged){
                        checkbounds(Currentlocation);
                    }
                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    @Override
    public void onLocationChanged(Location location) {
        Currentlocation = new LatLng(location.getLatitude(),location.getLongitude());
        locChanged = true;
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