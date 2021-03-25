package com.jeremydufeux.go4lunch.repositories;

import android.location.Location;
import android.util.Log;

import io.reactivex.subjects.BehaviorSubject;

public class UserDataRepository {
    private Location mLocation;
    private boolean mPermissionGranted;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private boolean mapViewDataSet;

    private static final UserDataRepository INSTANCE = new UserDataRepository();

    public static UserDataRepository getInstance(){
        return INSTANCE;
    }

    private UserDataRepository() {
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

    public void setMapViewCameraLatitude(double mapViewCameraLatitude) {
        this.mapViewCameraLatitude = mapViewCameraLatitude;
    }

    public double getMapViewCameraLongitude() {
        return mapViewCameraLongitude;
    }

    public void setMapViewCameraLongitude(double mapViewCameraLongitude) {
        this.mapViewCameraLongitude = mapViewCameraLongitude;
    }

    public float getMapViewCameraZoom() {
        return mapViewCameraZoom;
    }

    public void setMapViewCameraZoom(float mapViewCameraZoom) {
        this.mapViewCameraZoom = mapViewCameraZoom;
    }

    public boolean isMapViewDataSet() {
        return mapViewDataSet;
    }

    public void setMapViewDataSet(boolean mapViewDataSet) {
        this.mapViewDataSet = mapViewDataSet;
    }
}
