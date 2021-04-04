package com.jeremydufeux.go4lunch.mappers;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.HashMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RestaurantToMapViewMapper implements Function<HashMap<String, Restaurant>, HashMap<String, Restaurant>> {

    @Override
    public HashMap<String, Restaurant> apply(@NonNull HashMap<String, Restaurant> restaurantHashMap) {

        for(Restaurant restaurant : restaurantHashMap.values()){
            if(restaurant.getInterestedWorkmates().size() > 0){
                restaurant.setMarkerOptionIconResource(R.drawable.ic_pin_interested);
            } else {
                restaurant.setMarkerOptionIconResource(R.drawable.ic_pin_normal);
            }
        }

        return restaurantHashMap;
    }
}