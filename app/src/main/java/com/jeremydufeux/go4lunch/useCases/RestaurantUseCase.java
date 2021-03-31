package com.jeremydufeux.go4lunch.useCases;

import com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultMapper;
import com.jeremydufeux.go4lunch.mappers.PlaceDetailsResultMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RestaurantUseCase{
    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final PublishSubject<Exception> mErrorsObservable = PublishSubject.create();

    @Inject
    public RestaurantUseCase(GooglePlacesRepository googlePlacesRepository, RestaurantRepository restaurantRepository) {
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
    }

    public void getNearbyPlaces(double latitude, double longitude, double radius) {
        mDisposable.add(mGooglePlacesRepository.getNearbyPlaces(latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .map(new NearbyPlacesResultMapper())
                .subscribeWith(receiptResultFromNearbyPlaces()));
    }

    public void getDetailsForPlaceId(String placeId){
        mDisposable.add(mGooglePlacesRepository.getDetailsForPlaceId(placeId)
                .subscribeOn(Schedulers.io())
                .map(new PlaceDetailsResultMapper())
                .subscribeWith(receiptResultFromPlacesDetails()));
    }

    private DisposableObserver<HashMap<String, Restaurant>> receiptResultFromNearbyPlaces() {
        return new DisposableObserver<HashMap<String, Restaurant>>() {
            @Override
            public void onNext(@NonNull HashMap<String, Restaurant> restaurantHashMap) {
                for (Restaurant restaurant: restaurantHashMap.values()) {
                    getDetailsForPlaceId(restaurant.getUId());
                }

                mRestaurantRepository.replaceRestaurantList(restaurantHashMap);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                mErrorsObservable.onNext(new Exception(e));
            }
            @Override
            public void onComplete() {
            }
        };
    }

    private DisposableObserver<Restaurant> receiptResultFromPlacesDetails() {
        return new DisposableObserver<Restaurant>() {
            @Override
            public void onNext(@NonNull Restaurant restaurant) {
                mRestaurantRepository.addRestaurantDetails(restaurant);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mErrorsObservable.onNext(new Exception(e));
            }
            @Override
            public void onComplete() {
            }
        };
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantList();
    }

    public Observable<Exception> observeErrors(){
        return mErrorsObservable;
    }

    public void clearDisposable(){
        mDisposable.clear();
    }
}
