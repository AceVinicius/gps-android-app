package com.example.gps;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

public class MyLocationListener implements LocationListener {
    public static double Latitude;
    public static double Longitude;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
    }
}
