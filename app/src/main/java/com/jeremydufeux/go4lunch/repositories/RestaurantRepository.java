package com.jeremydufeux.go4lunch.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RestaurantRepository {
    private final HashMap<String,Restaurant> mRestaurantHashMap = new HashMap<>();
    private final MutableLiveData<HashMap<String,Restaurant>> mRestaurantListLiveData;
    private final MutableLiveData<HashMap<String,Restaurant>> mRestaurantDetailsListLiveData;

    private static final RestaurantRepository INSTANCE = new RestaurantRepository();

    public static RestaurantRepository getInstance(){
        return INSTANCE;
    }

    private RestaurantRepository() {
        mRestaurantListLiveData = new MutableLiveData<>();
        mRestaurantDetailsListLiveData = new MutableLiveData<>();
    }

    public void replaceRestaurantList(List<Restaurant> restaurants) {
        mRestaurantHashMap.clear();
        for(Restaurant restaurant : restaurants){
            mRestaurantHashMap.put(restaurant.getUId(), restaurant);
        }
        mRestaurantListLiveData.postValue(mRestaurantHashMap);
    }

    public void addRestaurantDetails(Restaurant restaurant) {
        mRestaurantHashMap.put(restaurant.getUId(), restaurant);
        mRestaurantDetailsListLiveData.postValue(mRestaurantHashMap);
    }

    public LiveData<HashMap<String,Restaurant>> observeRestaurantList() {
        return mRestaurantListLiveData;
    }

    public LiveData<HashMap<String,Restaurant>> observeRestaurantDetailsList() {
        return mRestaurantListLiveData;
    }

    public Restaurant getRestaurantWithId(String placeId) {
        return mRestaurantHashMap.get(placeId);
    }


}
