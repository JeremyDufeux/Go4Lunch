package com.jeremydufeux.go4lunch.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

}
