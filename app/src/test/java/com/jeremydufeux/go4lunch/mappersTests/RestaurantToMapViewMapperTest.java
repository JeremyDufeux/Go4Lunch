package com.jeremydufeux.go4lunch.mappersTests;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.RestaurantToMapViewMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RestaurantToMapViewMapperTest {

    @Test
    public void test_markerOptionIconResource_givenNoWorkmate(){
        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithNoData();

        RestaurantToMapViewMapper mapper = new RestaurantToMapViewMapper();
        HashMap<String, Restaurant> mappedRestaurantHashMap = mapper.apply(restaurantHashMap);

        assertEquals(R.drawable.ic_pin_normal, mappedRestaurantHashMap.get("ChIJH274sClwjEcRniBZAsyAtH0").getMarkerOptionIconResource());
    }
    @Test
    public void test_markerOptionIconResource_givenTwoWorkmate(){
        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithInterestedWorkmates();

        RestaurantToMapViewMapper mapper = new RestaurantToMapViewMapper();
        HashMap<String, Restaurant> mappedRestaurantHashMap = mapper.apply(restaurantHashMap);

        assertEquals(R.drawable.ic_pin_interested, mappedRestaurantHashMap.get("ChIJH274sClwjEcRniBZAsyAtH0").getMarkerOptionIconResource());
    }

    private HashMap<String, Restaurant> generateRestaurantHashMapWithNoData() {
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");

        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant.getUId(), restaurant);
        return restaurantHashMap;
    }

    private HashMap<String, Restaurant> generateRestaurantHashMapWithInterestedWorkmates() {
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        restaurant.getInterestedWorkmates().add("Db5e374sClwjEbqoF8ZAsyAtH0");
        restaurant.getInterestedWorkmates().add("Db5e374sClwjEbqoF8ZAsyAtH0");
        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant.getUId(), restaurant);
        return restaurantHashMap;
    }
}
