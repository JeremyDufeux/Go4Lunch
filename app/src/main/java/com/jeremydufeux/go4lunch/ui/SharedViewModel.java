package com.jeremydufeux.go4lunch.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.models.PlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.PlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.PlaceResult.PlaceResults;
import com.jeremydufeux.go4lunch.models.PlaceResult.Result;
import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class SharedViewModel extends ViewModel {

    // For MainActivity
    private final MutableLiveData<Boolean> userLogged;

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
                .subscribeWith(new DisposableObserver<PlaceResults>() {
                    @Override
                    public void onNext(@NonNull PlaceResults results) {
                        updatePlaceListNewResults(getPlacesFromResults(results));
                        if(results.getNextPageToken()!=null) {
                            getNextPageNearbyPlaces(results.getNextPageToken());
                        }
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
        // TODO To check
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDisposable.add(mPlacesDataRepository.getNextPageNearbyPlaces(pageToken)
                .subscribeWith(new DisposableObserver<PlaceResults>() {
                    @Override
                    public void onNext(@NonNull PlaceResults results) {
                        updatePlaceListNewResults(getPlacesFromResults(results));
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
                .subscribeWith(new DisposableObserver<PlaceDetailsResults>() {
                    @Override
                    public void onNext(@NonNull PlaceDetailsResults results) {
                        updatePlaceListWithDetails(getDetailsFromResults(results));
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

    private List<Place> getPlacesFromResults(PlaceResults results){
        List<Place> placeList = new ArrayList<>();

        for(Result result : results.getResults()){
            if(result.getBusinessStatus()!= null) {
                if (result.getBusinessStatus().equals("OPERATIONAL")) {

                    LatLng latLng = new LatLng(result.getGeometry().getLocation().getLat(),
                            result.getGeometry().getLocation().getLng());

                    Place place = new Place(result.getPlaceId(), result.getName(), latLng);

                    placeList.add(place);
                }
            }
        }

        return placeList;
    }

    private Place getDetailsFromResults(PlaceDetailsResults results){
        com.jeremydufeux.go4lunch.models.PlaceDetailsResult.Result placeDetail = results.getResult();

        LatLng latLng = new LatLng(placeDetail.getGeometry().getLocation().getLat(),
                placeDetail.getGeometry().getLocation().getLng());

        Place place = new Place(placeDetail.getPlaceId(), placeDetail.getName(), latLng);
        place.setAddress(getAddressFromAddressComponents(placeDetail.getAddressComponents()));
        place.setOpeningHours(placeDetail.getOpeningHours());
        if(placeDetail.getPhotos() != null) {
            place.setPhotoReference(placeDetail.getPhotos().get(0).getPhotoReference());
        }
        place.setPhoneNumber(placeDetail.getInternationalPhoneNumber());
        place.setWebsite(placeDetail.getWebsite());
        place.setRating(placeDetail.getRating());

        return place;
    }

    private String getAddressFromAddressComponents(List<AddressComponent> addressComponents){
        String streetNumber = "";
        String route = "";
        for (int i = 0; i < addressComponents.size(); i++){
            if(addressComponents.get(i).getTypes().contains("street_number")){
                streetNumber = addressComponents.get(i).getLongName() + ", ";
            } else if(addressComponents.get(i).getTypes().contains("route")){
                route = addressComponents.get(i).getLongName();
            }
        }
        return streetNumber + route;
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

    public MutableLiveData<List<Place>> getPlaceListLiveData() {
        return mPlaceListLiveData;
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
        mDisposable.clear();
    }
}
