package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;

public class RestaurantDetailsViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    public RestaurantDetailsViewModel(RestaurantRepository restaurantRepository) {
        mRestaurantRepository = restaurantRepository;
    }

    public Restaurant getRestaurantWithId(String placeId) {
        return mRestaurantRepository.getRestaurantWithId(placeId);
    }
}
