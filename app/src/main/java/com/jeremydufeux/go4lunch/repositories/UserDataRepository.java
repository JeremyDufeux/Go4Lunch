package com.jeremydufeux.go4lunch.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.jeremydufeux.go4lunch.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class UserDataRepository {
    private static final String FILENAME = "UserData";
    public static final String PREF_CAMERA_LAT = "PREF_CAMERA_LAT";
    public static final String PREF_CAMERA_LNG = "PREF_CAMERA_LNG";
    public static final String PREF_CAMERA_ZOOM = "PREF_CAMERA_ZOOM";
    public static final String PREF_NOTIFICATION_ENABLED = "PREF_NOTIFICATION_ENABLED";
    public static final String PREF_DISTANCE_UNIT = "PREF_DISTANCE_UNIT";

    private static SharedPreferences mPreferences;

    // For Location
    private Location mLocation;
    private boolean mPermissionGranted;
    private int mDistanceUnit = R.string.unit_meter_short;

    // For Map View
    private double mMapViewCameraLatitude;
    private double mMapViewCameraLongitude;
    private float mMapViewCameraZoom;
    private double mMapViewCameraRadius;
    private boolean mMapViewDataSet;
    private boolean mMapViewAlreadyStarted;

    // For Notifications
    private boolean mNotificationEnabled = true;

    @Inject
    UserDataRepository(@ApplicationContext Context context) {
        mPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        readPref();
    }

    // -------------
    // SharedPreferences
    // -------------

    public void savePreferences(){
        mPreferences.edit()
                .putLong(PREF_CAMERA_LAT, Double.doubleToRawLongBits(mMapViewCameraLatitude))
                .putLong(PREF_CAMERA_LNG, Double.doubleToRawLongBits(mMapViewCameraLongitude))
                .putFloat(PREF_CAMERA_ZOOM, mMapViewCameraZoom)
                .apply();
    }

    void readPref(){
        if(mPreferences.contains(PREF_CAMERA_LAT)) {
            mMapViewCameraLatitude =  Double.longBitsToDouble(mPreferences.getLong(PREF_CAMERA_LAT, 0));
            mMapViewCameraLongitude =  Double.longBitsToDouble(mPreferences.getLong(PREF_CAMERA_LNG, 0));
            mMapViewCameraZoom =  mPreferences.getFloat(PREF_CAMERA_ZOOM, 0);
            mNotificationEnabled = mPreferences.getBoolean(PREF_NOTIFICATION_ENABLED, true);
            mDistanceUnit = mPreferences.getInt(PREF_DISTANCE_UNIT, mDistanceUnit);
            mMapViewDataSet = true;
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

    public int getDistanceUnit() {
        return mDistanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        mDistanceUnit = distanceUnit;
        mPreferences.edit().putInt(PREF_DISTANCE_UNIT, mDistanceUnit).apply();
    }

    // -------------
    // For MapView
    // -------------

    public double getMapViewCameraLatitude() {
        return mMapViewCameraLatitude;
    }

    public double getMapViewCameraLongitude() {
        return mMapViewCameraLongitude;
    }

    public float getMapViewCameraZoom() {
        return mMapViewCameraZoom;
    }

    public double getMapViewCameraRadius() {
        return mMapViewCameraRadius;
    }

    public boolean isMapViewAlreadyStarted() {
        return mMapViewAlreadyStarted;
    }

    public boolean isMapViewDataSet() {
        return mMapViewDataSet;
    }

    public void setMapViewData(double latitude, double longitude, float zoom, double radius) {
        mMapViewCameraLatitude = latitude;
        mMapViewCameraLongitude = longitude;
        mMapViewCameraZoom = zoom;
        mMapViewCameraRadius = radius;
        mMapViewAlreadyStarted = true;
        mMapViewDataSet = true;
        savePreferences();
    }

    // -------------
    // For Notification
    // -------------

    public boolean isNotificationEnabled() {
        return mNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        mNotificationEnabled = notificationEnabled;
        mPreferences.edit().putBoolean(PREF_NOTIFICATION_ENABLED, mNotificationEnabled).apply();
    }
}
