package com.jeremydufeux.go4lunch.ui;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class SharedViewModel extends ViewModel {

    // For Location
    private Location mLocation;
    private final MutableLiveData<Boolean> mSystemSettingsDialogRequest;
    private final MutableLiveData<Boolean> mLocationGrantedLiveData;
    private final MutableLiveData<Location> mLocationLiveData;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private boolean mapViewDataSet;

    public SharedViewModel() {
        mLocationGrantedLiveData = new MutableLiveData<>();
        mLocationLiveData = new MutableLiveData<>();

        mSystemSettingsDialogRequest = new MutableLiveData<>();
    }

    // -------------
    // For Location
    // -------------

    public LiveData<Boolean> observeSystemSettingsDialogRequest() {
        return mSystemSettingsDialogRequest;
    }

    public void setSystemSettingsDialogRequest(boolean request){
        mSystemSettingsDialogRequest.setValue(request);
    }

    public LiveData<Boolean> observeLocationPermissionGranted(){
        return mLocationGrantedLiveData;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted){
        mLocationGrantedLiveData.setValue(locationPermissionGranted);
    }

    public void setUserLocation(Location location){
        mLocation = location;
        mLocationLiveData.postValue(mLocation);
    }

    public LiveData<Location> observeUserLocation(){
        return mLocationLiveData;
    }

    public Location getLocation() {
        return mLocation;
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
