package com.jeremydufeux.go4lunch.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantRepository {
    private final HashMap<String,Restaurant> mRestaurantHashMap = new HashMap<>();
    private final List<Restaurant> mRestaurantList = new ArrayList<>();
    private final MutableLiveData<List<Restaurant>> mRestaurantListLiveData;

    private static final RestaurantRepository INSTANCE = new RestaurantRepository();

    public static RestaurantRepository getInstance(){
        return INSTANCE;
    }

    private RestaurantRepository() {
        mRestaurantListLiveData = new MutableLiveData<>();
    }

    public boolean isRestaurantAlreadyInList(String uId){
        return mRestaurantHashMap.containsKey(uId);
    }

    public void addNewRestaurant(Restaurant restaurant) {
        if(!mRestaurantHashMap.containsKey(restaurant.getUId())) {
            mRestaurantHashMap.put(restaurant.getUId(), restaurant);
            mRestaurantList.add(restaurant);
            //mRestaurantListLiveData.postValue(mRestaurantList); // TODO To check
            mRestaurantListLiveData.postValue(new ArrayList<>(mRestaurantHashMap.values()));
        }
    }

    public LiveData<List<Restaurant>> observeRestaurantList() {
        return mRestaurantListLiveData;
    }

    public Restaurant getRestaurantWithId(String placeId) {
        return mRestaurantHashMap.get(placeId);
    }
}
