package com.jeremydufeux.go4lunch.mappers;


import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class NearbyPlacesResultToRestaurantMapper implements Function<PlaceSearchResults, List<Restaurant>>  {
    public static final String OPERATIONAL_BUSINESS_STATUS = "OPERATIONAL";

    @Override
    public List<Restaurant> apply(@NonNull PlaceSearchResults results) {
        List<Restaurant> restaurantList = new ArrayList<>();

        for(PlaceSearch placeSearch : results.getPlaceSearches()){
            if(placeSearch.getBusinessStatus()!= null && placeSearch.getBusinessStatus().equals(OPERATIONAL_BUSINESS_STATUS)) {
                Restaurant restaurant = new Restaurant(placeSearch.getPlaceId());
                restaurantList.add(restaurant);
            }
        }

        return restaurantList;
    }
}
