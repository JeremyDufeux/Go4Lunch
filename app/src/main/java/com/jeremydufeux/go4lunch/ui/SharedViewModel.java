package com.jeremydufeux.go4lunch.ui;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

                    double lat = result.getGeometry().getLocation().getLat();
                    double lng = result.getGeometry().getLocation().getLng();

                    Place place = new Place(result.getPlaceId(), result.getName(), lat, lng);

                    placeList.add(place);
                }
            }
        }

        return placeList;
    }

    private Place getDetailsFromResults(PlaceDetailsResults results){
        com.jeremydufeux.go4lunch.models.PlaceDetailsResult.Result placeDetail = results.getResult();

        double lat = placeDetail.getGeometry().getLocation().getLat();
        double lng = placeDetail.getGeometry().getLocation().getLng();

        Place place = new Place(placeDetail.getPlaceId(), placeDetail.getName(), lat, lng);

        String address = getAddressFromAddressComponents(placeDetail.getAddressComponents());
        if(address.isEmpty()){
            address = placeDetail.getVicinity();
        }
        place.setAddress(address);

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
