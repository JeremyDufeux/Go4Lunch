package com.jeremydufeux.go4lunch.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class UserDataRepository {
    private static final String FILENAME = "UserData";
    public static final String PREF_DATA_SET = "PREF_DATA_SET";
    public static final String PREF_CAMERA_LAT = "PREF_CAMERA_LAT";
    public static final String PREF_CAMERA_LNG = "PREF_CAMERA_LNG";
    public static final String PREF_CAMERA_ZOOM = "PREF_CAMERA_ZOOM";

    private static SharedPreferences mPreferences;

    private Location mLocation;
    private boolean mPermissionGranted;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private double mapViewCameraRadius;
    private boolean mapViewAlreadyStarted;

    @Inject
    UserDataRepository(@ApplicationContext Context context) {
        mPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        readPref();
    }

    // -------------
    // SharedPreferences
    // -------------

    public void savePreferences(){
        mPreferences.edit().putLong(PREF_CAMERA_LAT, Double.doubleToRawLongBits(mapViewCameraLatitude)).apply();
        mPreferences.edit().putLong(PREF_CAMERA_LNG, Double.doubleToRawLongBits(mapViewCameraLongitude)).apply();
        mPreferences.edit().putFloat(PREF_CAMERA_ZOOM, mapViewCameraZoom).apply();
    }

    void readPref(){
        if(mPreferences.contains(PREF_DATA_SET)) {
            mapViewCameraLatitude =  Double.longBitsToDouble(mPreferences.getLong(PREF_CAMERA_LAT, 0));
            mapViewCameraLongitude =  Double.longBitsToDouble(mPreferences.getLong(PREF_CAMERA_LNG, 0));
            mapViewCameraZoom =  mPreferences.getFloat(PREF_CAMERA_ZOOM, 0);
        }
    }

    // -------------
    // For Location
    // -------------

    public void setLocation(Location location){
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocationPermissionGranted(boolean granted) {
        mPermissionGranted = granted;
    }

    public boolean isPermissionGranted() {
        return mPermissionGranted;
    }

    // -------------
    // For MapView
    // -------------

    public double getMapViewCameraLatitude() {
        return mapViewCameraLatitude;
    }

    public double getMapViewCameraLongitude() {
        return mapViewCameraLongitude;
    }

    public float getMapViewCameraZoom() {
        return mapViewCameraZoom;
    }

    public double getMapViewCameraRadius() {
        return mapViewCameraRadius;
    }

    public boolean isMapViewAlreadyStarted() {
        return mapViewAlreadyStarted;
    }

    public void setMapViewData(double latitude, double longitude, float zoom, double radius) {
        mapViewCameraLatitude = latitude;
        mapViewCameraLongitude = longitude;
        mapViewCameraZoom = zoom;
        mapViewCameraRadius = radius;
        mapViewAlreadyStarted = true;
    }
}
