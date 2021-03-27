package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class RestaurantRepository {
    private final HashMap<String,Restaurant> mRestaurantHashMap;

    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantListObservable;
    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantDetailsListObservable;

    private static final RestaurantRepository INSTANCE = new RestaurantRepository();

    public static RestaurantRepository getInstance(){
        return INSTANCE;
    }

    private RestaurantRepository() {
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
    public Restaurant getRestaurantWithId(String placeId) {
        return mRestaurantHashMap.get(placeId);
    }




}
