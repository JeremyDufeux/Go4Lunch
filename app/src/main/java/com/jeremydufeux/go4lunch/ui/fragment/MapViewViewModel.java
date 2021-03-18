package com.jeremydufeux.go4lunch.ui.fragment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MapViewViewModel extends ViewModel {
    private static final String PLACE_TYPE_RESTAURANT = "restaurant";

    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;
    private final Executor mExecutor;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MapViewViewModel(GooglePlacesRepository googlePlacesRepository, RestaurantRepository restaurantRepository, Executor executor) {
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
        mExecutor = executor;
    }

    public void getNearbyPlaces(String latlng, String radius) {
        mDisposable.add(mGooglePlacesRepository.getNearbyPlaces(latlng, radius, PLACE_TYPE_RESTAURANT)
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
        for (Restaurant restaurant : restaurants){
            if(!mRestaurantRepository.isRestaurantAlreadyInList(restaurant.getUId())){
                getDetailsForPlaceId(restaurant.getUId());
            }
        }
    }

    private void receiptResultFromPlacesDetails(Restaurant restaurant) {
        mExecutor.execute(() ->
            mRestaurantRepository.addNewRestaurant(restaurant)
        );
    }

    public LiveData<List<Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantList();
    }
}
