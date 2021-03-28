package com.jeremydufeux.go4lunch.ui.fragment.mapView;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.useCases.RestaurantUseCase;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;

import java.util.HashMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.jeremydufeux.go4lunch.ui.fragment.mapView.MapViewFragment.DEFAULT_ZOOM_VALUE;
import static com.jeremydufeux.go4lunch.ui.fragment.mapView.MapViewFragment.LIMIT_ZOOM_VALUE;

public class MapViewViewModel extends ViewModel {
    private final RestaurantUseCase mRestaurantUseCase;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private MutableLiveData<HashMap<String, Restaurant>> mRestaurantListLiveData;

    private boolean mEnableFirstMoveToLocation = true;
    private boolean mFetchNearbyPlacesAfterCameraIdle = false;
    private boolean mCanShowSearchButton = false;
    private boolean mCanShowZoomSnackbar = false;
    private boolean mCanAddMarkers = false;
    private Location mLocation;

    public MapViewViewModel(RestaurantUseCase restaurantUseCase, UserDataRepository userDataRepository) {
        mRestaurantUseCase = restaurantUseCase;
        mUserDataRepository = userDataRepository;

        mRestaurantListLiveData = new MutableLiveData<>();

        mDisposable.add(mRestaurantUseCase.observeRestaurantList()
                .subscribeOn(Schedulers.io())
                .subscribeWith(getRestaurantList()));
    }

    public DisposableObserver<HashMap<String, Restaurant>> getRestaurantList(){
        return new DisposableObserver<HashMap<String, Restaurant>>() {
            @Override
            public void onNext(@NonNull HashMap<String, Restaurant> restaurantHashMap) {
                mRestaurantListLiveData.postValue(restaurantHashMap);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Debug", "onError getRestaurantList " + e.toString());
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public LiveData<HashMap<String, Restaurant>> observeRestaurantList(){
        return mRestaurantListLiveData;
    }

    public void getNearbyPlaces(double latitude, double longitude, double radius) {
        mRestaurantUseCase.getNearbyPlaces(latitude, longitude, radius);
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

    @Override
    protected void onCleared() {
        mRestaurantUseCase.clearDisposable();
        super.onCleared();
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

    // TODO Replace with live event
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
