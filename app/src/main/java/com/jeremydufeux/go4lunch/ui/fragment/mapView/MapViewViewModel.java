package com.jeremydufeux.go4lunch.ui.fragment.mapView;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.useCases.RestaurantUseCase;
import com.jeremydufeux.go4lunch.utils.LiveEvent.AddMarkersLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.FocusCameraLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.HideSearchButtonLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.OpenSystemSettingsLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.RemoveMarkersLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSearchButtonLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.jeremydufeux.go4lunch.ui.fragment.mapView.MapViewFragment.DEFAULT_ZOOM_VALUE;
import static com.jeremydufeux.go4lunch.ui.fragment.mapView.MapViewFragment.LIMIT_ZOOM_VALUE;

@HiltViewModel
public class MapViewViewModel extends ViewModel {
    private final RestaurantUseCase mRestaurantUseCase;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final MutableLiveData<HashMap<String, Restaurant>> mRestaurantListLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    private boolean mEnableFirstMoveToLocation = true;
    private boolean mFetchNearbyPlacesAfterCameraIdle = false;
    private boolean mCanShowSearchButton = false;
    private boolean mCanShowZoomSnackbar = false;
    private boolean mCanAddMarkers = false;
    private Location mLocation;

    @Inject
    public MapViewViewModel(RestaurantUseCase restaurantUseCase, UserDataRepository userDataRepository) {
        mRestaurantUseCase = restaurantUseCase;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers(){
        mDisposable.add(mRestaurantUseCase.observeErrors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getErrors()));

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
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public DisposableObserver<Exception> getErrors(){
        return new DisposableObserver<Exception>() {
            @Override
            public void onNext(@NonNull Exception exception) {
                if(exception instanceof TimeoutException){
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_timeout));
                }
                else if(exception instanceof UnknownHostException) {
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_no_internet));
                }
                else {
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
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
        if(location != null) {
            mLocation = location;
            mUserDataRepository.setLocation(mLocation);
            if (mEnableFirstMoveToLocation) { // First location reception, move camera to
                mFetchNearbyPlacesAfterCameraIdle = true;
                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mSingleLiveEvent.setValue(new FocusCameraLiveEvent(latLng, DEFAULT_ZOOM_VALUE, true));
                mEnableFirstMoveToLocation = false;
            }
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
            mSingleLiveEvent.setValue(new FocusCameraLiveEvent(latLng, mUserDataRepository.getMapViewCameraZoom(), false));
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
                mSingleLiveEvent.setValue(new ShowSearchButtonLiveEvent());
            } else {
                mCanShowSearchButton = true;
            }
            if(mCanAddMarkers){
                mSingleLiveEvent.setValue(new AddMarkersLiveEvent());
                mCanAddMarkers = false;
            }
            mCanShowZoomSnackbar = true;
        } else {
            mSingleLiveEvent.setValue(new HideSearchButtonLiveEvent());
            mSingleLiveEvent.setValue(new RemoveMarkersLiveEvent());

            if (mCanShowZoomSnackbar) {
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.zoom_to_see_restaurants));
                mCanShowZoomSnackbar = false;
            }
            mCanShowSearchButton = true;
            mCanAddMarkers = true;
        }
    }

    public void focusToLocation() {
        if(mUserDataRepository.isPermissionGranted()) {
            if(mLocation != null){
                mSingleLiveEvent.setValue(new FocusCameraLiveEvent(getLatLng(mLocation), DEFAULT_ZOOM_VALUE, true));
            } else{
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.position_unknown_for_now));
            }
        } else {
            mSingleLiveEvent.setValue(new OpenSystemSettingsLiveEvent());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables() {
        mRestaurantUseCase.clearDisposable();
        mDisposable.clear();
    }

    // -------------
    // Live Event
    // -------------

    public LiveData<LiveEvent> observeEvents(){
        return mSingleLiveEvent;
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
    
    // ---------------
    // Utils
    // ---------------

    private LatLng getLatLng(Location location){
        return  new LatLng(location.getLatitude(), location.getLongitude());
    }
}
