package com.jeremydufeux.go4lunch.ui.fragment;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.jeremydufeux.go4lunch.ui.fragment.MapViewFragment.DEFAULT_ZOOM_VALUE;
import static com.jeremydufeux.go4lunch.ui.fragment.MapViewFragment.LIMIT_ZOOM_VALUE;

public class MapViewViewModel extends ViewModel {
    private static final String PLACE_TYPE_RESTAURANT = "restaurant";

    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;
    private final UserDataRepository mUserDataRepository;
    private final Executor mExecutor;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private boolean mEnableFirstMoveToLocation = true;
    private boolean mFetchNearbyPlacesAfterCameraIdle = false;
    private boolean mCanShowSearchButton = false;
    private boolean mCanShowZoomSnackbar = false;
    private boolean mCanAddMarkers = false;
    private Location mLocation;

    public MapViewViewModel(GooglePlacesRepository googlePlacesRepository, RestaurantRepository restaurantRepository, UserDataRepository userDataRepository, Executor executor) {
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
        mUserDataRepository = userDataRepository;
        mExecutor = executor;
    }

    public Observable<HashMap<String, Restaurant>> onRestaurantListChanged() {
        return mRestaurantRepository.observeRestaurantList();
    }

    public void getNearbyPlaces(double latitude, double longitude, double radius) {
        mDisposable.add(mGooglePlacesRepository.getNearbyPlaces(latitude, longitude, radius, PLACE_TYPE_RESTAURANT)
                .subscribeOn(Schedulers.io()) // TODO To check
                .subscribeWith(new DisposableObserver<List<Restaurant>>() {
                    @Override
                    public void onNext(@NonNull List<Restaurant> result) {
                        receiptResultFromNearbyPlaces(result);
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
        mDisposable.add(mGooglePlacesRepository.getDetailsForPlaceId(placeId)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Restaurant>() {
                    @Override
                    public void onNext(@NonNull Restaurant restaurant) {
                        receiptResultFromPlacesDetails(restaurant);
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

    private void receiptResultFromNearbyPlaces(List<Restaurant> restaurants) {
        mRestaurantRepository.replaceRestaurantList(restaurants);
        for (Restaurant restaurant : restaurants){
            getDetailsForPlaceId(restaurant.getUId());
        }
    }

    private void receiptResultFromPlacesDetails(Restaurant restaurant) {
        mExecutor.execute(() ->
            mRestaurantRepository.addRestaurantDetails(restaurant)
        );
    }

    public void setLocation(Location location) {
        mLocation = location;
        mUserDataRepository.setLocation(mLocation);
        if(mEnableFirstMoveToLocation){ // First location reception, move camera to
            mFetchNearbyPlacesAfterCameraIdle = true;
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mCallback.focusCamera(latLng, DEFAULT_ZOOM_VALUE, true);
            mEnableFirstMoveToLocation = false;
        }
    }

    public void setCanShowSearchButton(boolean canShowSearchButton) {
        mCanShowSearchButton = canShowSearchButton;
    }

    public void setLocationPermissionGranted(boolean granted) {
        mUserDataRepository.setLocationPermissionGranted(granted);
    }

    public void checkForSavedData() {
        if(mUserDataRepository.isMapViewDataSet()){
            LatLng latLng = new LatLng( mUserDataRepository.getMapViewCameraLatitude(),
                                        mUserDataRepository.getMapViewCameraLongitude());
            mCallback.focusCamera(latLng, mUserDataRepository.getMapViewCameraZoom(), false);
            mEnableFirstMoveToLocation = false;
        }
    }


    public void onCameraIdle(double latitude, double longitude, float zoom, double radius) {
        if(mFetchNearbyPlacesAfterCameraIdle){
            getNearbyPlaces(latitude, longitude, radius);
            mFetchNearbyPlacesAfterCameraIdle = false;
            mCanShowSearchButton = false;
            mCanShowZoomSnackbar = true;
            mCanAddMarkers = true;
        }

        if (zoom > LIMIT_ZOOM_VALUE) {
            if (mCanShowSearchButton) {
                mCallback.showSearchButton();
            } else {
                mCanShowSearchButton = true;
            }
            if(mCanAddMarkers){
                mCallback.addMarkers();
                mCanAddMarkers = false;
            }
            mCanShowZoomSnackbar = true;
        } else {
            mCallback.hideSearchButton();
            mCallback.removeMarkers();

            if (mCanShowZoomSnackbar) {
                mCallback.showSnackBar(R.string.zoom_to_see_restaurants);
                mCanShowZoomSnackbar = false;
            }
            mCanShowSearchButton = true;
            mCanAddMarkers = true;
        }
    }

    public void focusToLocation() {
        if(mUserDataRepository.isPermissionGranted()) {
            mCallback.focusCamera(getLatLng(mLocation), DEFAULT_ZOOM_VALUE, true);
        } else {
            mCallback.openSystemSettingsDialog();
        }
    }

    // -------------
    // Map saved data
    // -------------

    public void saveCameraData(double latitude, double longitude, float zoom) {
        mUserDataRepository.setMapViewCameraLatitude(latitude);
        mUserDataRepository.setMapViewCameraLongitude(longitude);
        mUserDataRepository.setMapViewCameraZoom(zoom);
        mUserDataRepository.setMapViewDataSet(true);
    }

    // -------------
    // Map Callback
    // -------------

    private MapViewCallback mCallback;

    public void setMapViewCallback(MapViewCallback callback){
        mCallback = callback;
    }

    public interface MapViewCallback{
        void focusCamera(LatLng latLng, float zoom, boolean animCamera);
        void openSystemSettingsDialog();
        void showSearchButton();
        void hideSearchButton();
        void showSnackBar(int stringId);
        void addMarkers();
        void removeMarkers();
    }

    // ---------------
    // Utils
    // ---------------

    private LatLng getLatLng(Location location){
        return  new LatLng(location.getLatitude(), location.getLongitude());
    }
}
