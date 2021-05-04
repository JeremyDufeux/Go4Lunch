package com.jeremydufeux.go4lunch.repositoriesTests;

import com.jeremydufeux.go4lunch.models.OpenPeriod;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestaurantRepositoryTest {
    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    RestaurantRepository mRestaurantRepository;

    @Before
    public void initRestaurantRepository() {
        mRestaurantRepository = new RestaurantRepository();
    }

    @Test
    public void test_addNewRestaurant_checkRestaurantIdAndName() {
        Restaurant restaurant = generateRestaurant();

        mRestaurantRepository.setNewListSize(1);
        mRestaurantRepository.addNewRestaurant(restaurant);

        mRestaurantRepository.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap ->
                        restaurantHashMap.containsValue(restaurant)
                )
                .dispose();
    }

    @Test
    public void test_getRestaurantWithId_checkRestaurant(){
        Restaurant restaurant = generateRestaurantDetails();
        mRestaurantRepository.addNewRestaurant(restaurant);

        mRestaurantRepository.getRestaurantWithId(restaurant.getUId())
                .test()
                .assertValue(restaurant);
    }

    @Test
    public void test_isRestaurantPresent(){
        Restaurant restaurant = generateRestaurantDetails();
        mRestaurantRepository.addNewRestaurant(restaurant);

        mRestaurantRepository.isRestaurantPresent(restaurant.getUId())
            .test()
            .assertValue(true);

    }

    // ---------------
    // Generate data
    // ---------------

    private Restaurant generateRestaurant(){
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        restaurant.setName("Le viand'art");

        return restaurant;
    }

    private Restaurant generateRestaurantDetails(){
        Restaurant restaurant = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        restaurant.setName("Le viand'art");
        restaurant.setAddress("56 Rue de Genève, Ambilly");
        restaurant.setUtcOffset(7200000);
        restaurant.setPhoneNumber("+33 4 50 92 80 69");
        restaurant.setWebsite("https://le-viandart.business.site/");
        restaurant.setRating(4.2f);
        restaurant.setPhotoUrl("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp");

        List<OpenPeriod> openingHours = new ArrayList<>();
        for(int i = 0; i < Calendar.DAY_OF_WEEK; i++){
            OpenPeriod openPeriod = new OpenPeriod(1, 10, 0, 1, 20, 0);
            openingHours.add(openPeriod);
        }
        restaurant.setOpeningPeriods(openingHours);
        restaurant.setOpeningHoursAvailable(true);
        restaurant.setAlwaysOpen(false);

        return restaurant;
    }
}
