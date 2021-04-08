package com.jeremydufeux.go4lunch.repositories;

import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserDataRepository {
    private Location mLocation;
    private boolean mPermissionGranted;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private double mapViewCameraRadius;
    private boolean mapViewDataSet;

    @Inject
    UserDataRepository() {
    }

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

    public boolean isMapViewDataSet() {
        return mapViewDataSet;
    }

    public void setMapViewData(double latitude, double longitude, float zoom, double radius) {
        mapViewCameraLatitude = latitude;
        mapViewCameraLongitude = longitude;
        mapViewCameraZoom = zoom;
        mapViewCameraRadius = radius;
        mapViewDataSet = true;
    }
}
