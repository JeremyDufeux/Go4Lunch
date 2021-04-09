package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class RestaurantRepository {
    private final HashMap<String,Restaurant> mRestaurantHashMap;
    private int newListSize;
    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantListObservable;
    private final BehaviorSubject<HashMap<String, Restaurant>> mRestaurantDetailsListObservable;

    @Inject
    RestaurantRepository() {
        mRestaurantHashMap = new HashMap<>();

        mRestaurantListObservable = BehaviorSubject.create();
        mRestaurantDetailsListObservable = BehaviorSubject.create();
        mRestaurantDetailsListObservable.onNext(mRestaurantHashMap);
    }

    public void addNewRestaurant(Restaurant restaurant) {
        mRestaurantHashMap.put(restaurant.getUId(), restaurant);

        if(mRestaurantHashMap.size() == newListSize) {
            mRestaurantListObservable.onNext(new HashMap<>(mRestaurantHashMap));
        }
    }
    public void updateRestaurant(Restaurant restaurant) {
        mRestaurantHashMap.put(restaurant.getUId(), restaurant);

        if(mRestaurantHashMap.size() == newListSize) {
            mRestaurantDetailsListObservable.onNext(mRestaurantHashMap);
        }
    }

    public void clearRestaurantList(){
        mRestaurantHashMap.clear();
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantList() {
        return mRestaurantListObservable;
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantDetailList() {
        return mRestaurantDetailsListObservable;
    }

    public Observable<Restaurant> getRestaurantWithId(String placeId) {
        return Observable.just(Objects.requireNonNull(mRestaurantHashMap.get(placeId)));
    }
    public Observable<Boolean> isRestaurantPresent(String placeId) {
        return Observable.just(mRestaurantHashMap.containsKey(placeId));
    }

    public void setNewListSize(int size) {
        newListSize = size;
        if(newListSize == 0){
            mRestaurantHashMap.clear();
            mRestaurantListObservable.onNext(new HashMap<>(mRestaurantHashMap));
        }
    }

    public void setAddListSize(int size) {
        newListSize += size;
    }
}
