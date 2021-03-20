package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ListViewViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    public ListViewViewModel(RestaurantRepository restaurantRepository) {
        mRestaurantRepository = restaurantRepository;
    }

    public LiveData<HashMap<String,Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantDetailsList();
    }

}
