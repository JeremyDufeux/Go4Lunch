package com.jeremydufeux.go4lunch.mappersTests;

import android.location.Location;
import android.view.View;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.RestaurantToListViewMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestaurantToListViewMapperTest {

    @Test
    public void test_calculateDistanceFromUser_givenLocation() {
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        restaurant.setLocation(new Location(""));
        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant.getUId(), restaurant);

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(new Location(""), R.string.unit_meter_short, Calendar.getInstance());
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(restaurantHashMap.size(), restaurantList.size());
        assertEquals(restaurantList.get(0).getDistanceTvVisibility(), View.VISIBLE);
    }

    @Test
    public void test_calculateDistanceFromUser_givenNoLocation() {
        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithNoData();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, Calendar.getInstance());
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(restaurantHashMap.size(), restaurantList.size());
        assertEquals(restaurantList.get(0).getDistanceTvVisibility(), View.INVISIBLE);
    }

    @Test
    public void test_determineOpening_checkOpenNowFirstPeriod() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 10);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.open_now, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.grey, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkOpenNowSecondPeriod() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 15);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.open_now, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.grey, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkOpenNowThirdPeriod() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 20); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 10);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.open_now, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.grey, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkCloseSoon(){
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 11);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.open_until, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.grey, restaurantList.get(0).getOpenTvColor());
        assertEquals("12:00 PM", restaurantList.get(0).getOpenTvCloseTimeString());
    }

    @Test
    public void test_determineOpening_checkClosedBeforeTime() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 7);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.closed, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.red, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkClosedAfterTime() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 20);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.closed, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.red, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkClosedOnSunday() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 18); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 20);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.closed, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.red, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkClosedBetweenPeriods() {
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2021);
        testDate.set(Calendar.MONTH, Calendar.APRIL); // April
        testDate.set(Calendar.DAY_OF_MONTH, 19); // Monday
        testDate.set(Calendar.HOUR_OF_DAY, 12);
        testDate.set(Calendar.MINUTE, 30);

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithOpenPeriods();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.closed, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.red, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineOpening_checkAlwaysOpen() {
        Calendar testDate = Calendar.getInstance();

        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithAlwaysOpe();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, testDate);
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(R.string.open_now, restaurantList.get(0).getOpenTvString());
        assertEquals(View.VISIBLE, restaurantList.get(0).getOpenTvVisibility());
        assertEquals(R.color.grey, restaurantList.get(0).getOpenTvColor());
    }

    @Test
    public void test_determineWorkmatesViewVisibility_checkWithNoWorkmates() {
        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithNoData();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, Calendar.getInstance());
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(View.INVISIBLE, restaurantList.get(0).getWorkmateIvVisibility());
        assertEquals(View.INVISIBLE, restaurantList.get(0).getWorkmateTvVisibility());
    }
    @Test
    public void test_determineWorkmatesViewVisibility_checkWithTwoWorkmates() {
        HashMap<String, Restaurant> restaurantHashMap = generateRestaurantHashMapWithInterestedWorkmates();

        RestaurantToListViewMapper mapper = new RestaurantToListViewMapper(null, R.string.unit_meter_short, Calendar.getInstance());
        List<Restaurant> restaurantList = mapper.apply(restaurantHashMap);

        assertEquals(View.VISIBLE, restaurantList.get(0).getWorkmateIvVisibility());
        assertEquals(View.VISIBLE, restaurantList.get(0).getWorkmateTvVisibility());
        assertEquals(2, restaurantList.get(0).getInterestedWorkmates().size());
    }

    private HashMap<String, Restaurant> generateRestaurantHashMapWithNoData() {
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");

        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant.getUId(), restaurant);
        return restaurantHashMap;
    }


    private HashMap<String, Restaurant> generateRestaurantHashMapWithOpenPeriods(){
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        HashMap<Integer, List<Restaurant.OpenPeriod>> openingHours = new HashMap<>();
        List<Restaurant.OpenPeriod> dayHours = new ArrayList<>();
        dayHours.add(new Restaurant.OpenPeriod(10, 0, 12, 0));
        dayHours.add(new Restaurant.OpenPeriod(14, 0, 18, 0));
        openingHours.put(1, dayHours);
        openingHours.put(2, dayHours);
        openingHours.put(4, dayHours);
        openingHours.put(5, dayHours);

        restaurant.setOpeningHours(openingHours);
        restaurant.setOpeningHoursAvailable(true);
        restaurant.setAlwaysOpen(false);
        restaurant.setUtcOffset(7200000);

        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant.getUId(), restaurant);
        return restaurantHashMap;
    }

    private HashMap<String, Restaurant> generateRestaurantHashMapWithAlwaysOpe (){
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        restaurant.setOpeningHoursAvailable(true);
        restaurant.setAlwaysOpen(true);
        restaurant.setUtcOffset(7200000);

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
