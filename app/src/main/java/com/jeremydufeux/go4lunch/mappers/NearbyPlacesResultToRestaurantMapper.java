package com.jeremydufeux.go4lunch.mappers;


import android.util.Log;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class NearbyPlacesResultToRestaurantMapper implements Function<PlaceSearchResults, List<Restaurant>>  {

    @Override
    public List<Restaurant> apply(@NonNull PlaceSearchResults results) {
        List<Restaurant> restaurantList = new ArrayList<>();

        for(PlaceSearch placeSearch : results.getPlaceSearches()){
            if(placeSearch.getBusinessStatus()!= null && placeSearch.getBusinessStatus().equals("OPERATIONAL")) {

                String uId = placeSearch.getPlaceId();

                double lat = placeSearch.getGeometry().getLocation().getLat();
                double lng = placeSearch.getGeometry().getLocation().getLng();

                Restaurant restaurant = new Restaurant(uId, placeSearch.getName(), lat, lng);

                restaurantList.add(restaurant);
            }
        }

        return restaurantList;
    }
}
