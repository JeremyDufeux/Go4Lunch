package com.jeremydufeux.go4lunch.useCases;

import android.util.Log;

import com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultToRestaurantMapper;
import com.jeremydufeux.go4lunch.mappers.PlaceDetailsResultToRestaurantMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.NoMorePageException;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RestaurantUseCase{
    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;
    private final WorkmatesRepository mWorkmatesRepository;

    private Disposable mDisposable;

    private final PublishSubject<Exception> mErrorsObservable = PublishSubject.create();

    @Inject
    public RestaurantUseCase(GooglePlacesRepository googlePlacesRepository,
                             RestaurantRepository restaurantRepository,
                             WorkmatesRepository workmatesRepository){
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
        mWorkmatesRepository = workmatesRepository;
    }

    public void getNearbyPlaces(double latitude, double longitude, double radius) {
        mRestaurantRepository.clearRestaurantList();
        mDisposable = getNearbyPlacesObserver(latitude, longitude, radius)
                .subscribe(
                        mRestaurantRepository::updateRestaurant,
                        throwable -> {
                            mErrorsObservable.onNext(new Exception(throwable));
                            Log.e("RestaurantUseCase", "getNearbyPlaces: " + throwable.toString());
                        });
    }

    private Observable<Restaurant> getNearbyPlacesObserver(double latitude, double longitude, double radius){
        return mGooglePlacesRepository.getNearbyPlaces(latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .map(new NearbyPlacesResultToRestaurantMapper())
                .map(restaurants -> {
                    mRestaurantRepository.setNewListSize(restaurants.size());
                    return restaurants;
                })
                .switchMap((Function<List<Restaurant>, ObservableSource<Restaurant>>)
                        Observable::fromIterable)
                .flatMap((Function<Restaurant, ObservableSource<Restaurant>>)
                        this::getInterestedWorkmates)
                .flatMap((Function<Restaurant, ObservableSource<Restaurant>>)
                        this::getDetailsForPlace)
                ;
    }

    private Observable<Restaurant> getInterestedWorkmates(Restaurant restaurant){
        return mWorkmatesRepository.getInterestedWorkmatesForRestaurants(restaurant.getUId())
                .subscribeOn(Schedulers.io())
                .map(interestedWorkmates -> {
                    restaurant.getInterestedWorkmates().clear();
                    for(Workmate workmate : interestedWorkmates) {
                        restaurant.getInterestedWorkmates().add(workmate.getUId());
                    }
                    mRestaurantRepository.addNewRestaurant(restaurant);
                    return restaurant;
                });
    }

    private Observable<Restaurant> getDetailsForPlace(Restaurant restaurant){
        return mGooglePlacesRepository.getDetailsForPlaceId(restaurant.getUId())
                .subscribeOn(Schedulers.io())
                .map(new PlaceDetailsResultToRestaurantMapper(restaurant));
    }

    public void loadNextPage() {
        if(mGooglePlacesRepository.haveNextPageToken()) {
            mDisposable = getNextPagePlacesObserver()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            mRestaurantRepository::updateRestaurant,
                            throwable -> {
                                mErrorsObservable.onNext(new Exception(throwable));
                                Log.e("RestaurantUseCase", "getNearbyPlaces: " + throwable.toString());
                            });
        } else {
            mErrorsObservable.onNext(new NoMorePageException());
        }
    }

    private Observable<Restaurant> getNextPagePlacesObserver(){
        return mGooglePlacesRepository.getNextPageNearbyPlaces()
                .subscribeOn(Schedulers.io())
                .map(new NearbyPlacesResultToRestaurantMapper())
                .map(restaurants -> {
                    mRestaurantRepository.setAddListSize(restaurants.size());
                    return restaurants;
                })
                .switchMap((Function<List<Restaurant>, ObservableSource<Restaurant>>)
                        Observable::fromIterable)
                .flatMap((Function<Restaurant, ObservableSource<Restaurant>>)
                        this::getInterestedWorkmates)
                .flatMap((Function<Restaurant, ObservableSource<Restaurant>>)
                        this::getDetailsForPlace)
                ;
    }

    public Observable<Restaurant> getRestaurantWithId(String restaurantId){
        return mRestaurantRepository.isRestaurantPresent(restaurantId)
                .flatMap((Function<Boolean, ObservableSource<Restaurant>>) restaurantPresent -> {
                    if(restaurantPresent){
                        return mRestaurantRepository.getRestaurantWithId(restaurantId);
                    }else {
                        return getDetailsForPlace(new Restaurant(restaurantId));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantList();
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantDetailsList(){
        return mRestaurantRepository.observeRestaurantDetailList();
    }

    public Observable<Exception> observeErrors(){
        return mErrorsObservable;
    }

    public void clearDisposable(){
        mDisposable.dispose();
    }
}
