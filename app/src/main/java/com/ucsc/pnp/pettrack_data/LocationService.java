package com.ucsc.pnp.pettrack_data;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by nrv on 9/17/16.
 */
public class LocationService extends Service implements LocationListener{
    int id=12558;
    String petName="Tom";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationService();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1, locationListener);

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        JSONObject msg=generatemessage(location);
        sendMessage(msg);
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

    public void sendMessage(final JSONObject msg){
        Runnable runble=new Runnable() {
            @Override
            public void run() {
                flushToNetwork(msg.toString());
            }
        };

        Thread thread=new Thread(runble);
        thread.start();

    }

    public JSONObject generatemessage(Location locdata){


        JSONObject message=new JSONObject();

        try {
            message.put("id",id);
            message.put("petname",petName);
            message.put("speed",locdata.getSpeed());
            message.put("time",getSystemtime());
            message.put("lat",locdata.getLatitude());
            message.put("lon",locdata.getLongitude());
            message.put("heartrate",randomHeartBeatGenerator());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public void flushToNetwork(String msg) {

        Socket smtpSocket = null;
        DataOutputStream os = null;


        try {
            smtpSocket = new Socket("192.34.63.88", 8072);

            os = new DataOutputStream(smtpSocket.getOutputStream());
        } catch (UnknownHostException e) {

        } catch (IOException e) {

        }
        if (smtpSocket != null && os != null) {
            try {
                os.writeBytes(msg);


                os.close();
                smtpSocket.close();
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }

    }

    public int randomHeartBeatGenerator(){
        Random randomgen=new Random();
        int newrandhb = randomgen.nextInt(30);
        return 75+newrandhb;
    }
    public long getSystemtime(){
        return System.currentTimeMillis();
    }

}
