package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ListViewViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    public ListViewViewModel(RestaurantRepository restaurantRepository, UserDataRepository userDataRepository) {
        mRestaurantRepository = restaurantRepository;
    }

    public Observable<List<Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantDetailsList()
                .subscribeOn(Schedulers.io())
                .map(stringRestaurantHashMap -> {
                    List<Restaurant> restaurantList = new ArrayList<>();
                    for(Restaurant restaurant : stringRestaurantHashMap.values()){
                        restaurant.calculateDistanceFromUser();
                        restaurant.determineOpening();
                        restaurantList.add(restaurant);
                    }
                    return restaurantList;
        });
    }

}
