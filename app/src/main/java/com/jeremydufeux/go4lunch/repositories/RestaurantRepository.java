package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class RestaurantRepository {
    private final HashMap<String,Restaurant> mRestaurantHashMap;

    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantListObservable;
    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantDetailsListObservable;

    @Inject
    RestaurantRepository() {
        mRestaurantHashMap = new HashMap<>();

        mRestaurantListObservable = BehaviorSubject.create();
        mRestaurantDetailsListObservable = BehaviorSubject.create();
    }

    public void replaceRestaurantList(HashMap<String, Restaurant> restaurants) {
        mRestaurantHashMap.clear();
        mRestaurantHashMap.putAll(restaurants);
        mRestaurantListObservable.onNext(new HashMap<>(mRestaurantHashMap));
    }

    public void addRestaurantDetails(Restaurant restaurant) {
        mRestaurantHashMap.put(restaurant.getUId(), restaurant);
        mRestaurantDetailsListObservable.onNext(mRestaurantHashMap);
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantList() {
        return mRestaurantListObservable;
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantDetailsList() {
        return mRestaurantDetailsListObservable;
    }
    public Observable<Restaurant> getRestaurantWithId(String placeId) {
        return Observable.just(mRestaurantHashMap.get(placeId));
    }




}
