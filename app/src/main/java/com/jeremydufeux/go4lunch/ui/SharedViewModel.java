package com.jeremydufeux.go4lunch.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;
import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class SharedViewModel extends ViewModel {

    // For MainActivity
    private MutableLiveData<Boolean> userLogged;

    // For List and Map View
    PlacesDataRepository mPlacesDataRepository;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<GooglePlaceResults> mGooglePlaceList;

    // For Map View
    private double mapViewCameraLatitude;
    private double mapViewCameraLongitude;
    private float mapViewCameraZoom;
    private boolean mapViewDataSet;

    public SharedViewModel(PlacesDataRepository placesDataRepository) {
        mPlacesDataRepository = placesDataRepository;

        mGooglePlaceList = new MutableLiveData<>();
        userLogged = new MutableLiveData<>();
    }

    // -------------
    // For MainActivity
    // -------------

    public LiveData<Boolean> isUserLogged(){
        return userLogged;
    }

    public void setUserLogged(boolean logged){
        userLogged.postValue(logged);
    }

    // -------------
    // For List and Map View
    // -------------



    public void getNearbyPlaces(String latlng, String radius, String type) {
        mDisposable.add(mPlacesDataRepository.getNearbyPlaces(latlng, radius, type)
                .subscribeWith(new DisposableObserver<GooglePlaceResults>() {
                    @Override
                    public void onNext(@NonNull GooglePlaceResults googlePlacesResults) {
                        mGooglePlaceList.postValue(googlePlacesResults);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Debug", "onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    public MutableLiveData<GooglePlaceResults> getGooglePlaceList() {
        return mGooglePlaceList;
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null) {
            mDisposable.clear();
        }
    }
}
