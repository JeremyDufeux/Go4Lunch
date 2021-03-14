package com.jeremydufeux.go4lunch.ui;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;

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

    // For List and Map View
    private final PlacesDataRepository mPlacesDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final HashMap<String, Place> mPlaceList;
    private final MutableLiveData<List<Place>> mPlaceListLiveData;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private boolean mapViewDataSet;

    public SharedViewModel(PlacesDataRepository placesDataRepository) {
        mPlacesDataRepository = placesDataRepository;

        mPlaceList = new HashMap<>();
        mPlaceListLiveData = new MutableLiveData<>();

        mLocationGrantedLiveData = new MutableLiveData<>();
        mLocationLiveData = new MutableLiveData<>();

        mSystemSettingsDialogRequest = new MutableLiveData<>();
    }

    // -------------
    // For List and Map View
    // -------------

    public void getNearbyPlaces(String latlng, String radius, String type) {
        mDisposable.add(mPlacesDataRepository.getNearbyPlaces(latlng, radius, type)
                .subscribeWith(new DisposableObserver<Pair<List<Place>, String>>() {
                    @Override
                    public void onNext(@NonNull Pair<List<Place>, String> placeListWithNextPageToken) {
                        getPlaceSearchResult(placeListWithNextPageToken);
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Debug", "onError getNearbyPlaces " + e.toString());
                    }
                    @Override
                    public void onComplete() {
                    }
                }));
    }

    public void getNextPageNearbyPlaces(String pageToken) {
        mDisposable.add(Observable.just("")
                        .delay(2, TimeUnit.SECONDS)
                        .flatMap(s -> mPlacesDataRepository.getNextPageNearbyPlaces(pageToken))
                        .subscribeWith(new DisposableObserver<Pair<List<Place>, String>>() {
                            @Override
                            public void onNext(@NonNull Pair<List<Place>, String> placeListWithNextPageToken) {
                                getPlaceSearchResult(placeListWithNextPageToken);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d("Debug", "onError getNearbyPlaces " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));
    }

    public void getDetailsForPlaceId(String placeId){
        mDisposable.add(mPlacesDataRepository.getDetailsForPlaceId(placeId)
                .subscribeWith(new DisposableObserver<Place>() {
                    @Override
                    public void onNext(@NonNull Place place) {
                        updatePlaceListWithDetails(place);
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Debug", "onError getDetailsForPlaceId " + e.toString());
                    }
                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void getPlaceSearchResult(Pair<List<Place>, String> placeListWithNextPageToken){
        assert placeListWithNextPageToken.first != null;
        updatePlaceListNewResults(placeListWithNextPageToken.first);
        if(placeListWithNextPageToken.second != null && !placeListWithNextPageToken.second.isEmpty()){
            getNextPageNearbyPlaces(placeListWithNextPageToken.second);
        }
    }

    private void updatePlaceListNewResults(List<Place> places) {
        for (Place place : places){
            if(!mPlaceList.containsKey(place.getUId())){
                mPlaceList.put(place.getUId(), place);
                getDetailsForPlaceId(place.getUId());
            }
        }
        mPlaceListLiveData.postValue(new ArrayList<>(mPlaceList.values()));
    }

    private void updatePlaceListWithDetails(Place placeDetails) {
        mPlaceList.put(placeDetails.getUId(), placeDetails);

        mPlaceListLiveData.postValue(new ArrayList<>(mPlaceList.values()));
    }

    public MutableLiveData<List<Place>> observePlaceList() {
        return mPlaceListLiveData;
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

    // -------------
    // For View Model
    // -------------

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }
}
