package com.jeremydufeux.go4lunch.mappers;

import android.location.Location;
import android.view.View;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RestaurantToListViewMapper implements Function<HashMap<String, Restaurant>, List<Restaurant>> {
    Location mLocation;

    public RestaurantToListViewMapper(Location location) {
        mLocation = location;
    }

    @Override
    public List<Restaurant> apply(@NonNull HashMap<String, Restaurant> restaurantHashMap) {
        List<Restaurant> restaurantList = new ArrayList<>();

        for(Restaurant restaurant : restaurantHashMap.values()){
            calculateDistanceFromUser(restaurant);
            determineOpening(restaurant);
            determineWorkmatesViewVisibility(restaurant);
            restaurantList.add(restaurant);
        }
        Collections.sort(restaurantList);

        return restaurantList;
    }

    public void calculateDistanceFromUser(Restaurant restaurant) {
        if(mLocation != null) {
            restaurant.setDistanceFromUser((int)mLocation.distanceTo(restaurant.getLocation()));
            restaurant.setDistanceTvVisibility(View.VISIBLE);
        } else {
            restaurant.setDistanceTvVisibility(View.INVISIBLE);
        }
    }

    private void determineOpening(Restaurant restaurant) {
        restaurant.setOpenTvColor(R.color.grey);

        if(restaurant.isOpeningHoursAvailable()) {
            restaurant.setOpenTvCloseTimeString(getClosingSoonTime(restaurant));
            if (restaurant.isOpenNow()) {
                if (!restaurant.getOpenTvCloseTimeString().isEmpty() && !restaurant.isAlwaysOpen()) {
                    restaurant.setOpenTvString(R.string.open_until);
                } else {
                    restaurant.setOpenTvString(R.string.open_now);
                }
            } else {
                restaurant.setOpenTvString(R.string.closed);
                restaurant.setOpenTvColor(R.color.red);
            }
            restaurant.setOpenTvVisibility(View.VISIBLE);
        } else {
            restaurant.setOpenTvString(R.string.no_open_hours);
            restaurant.setOpenTvVisibility(View.INVISIBLE);
        }
    }

    // If closing soon, return the closing time, else return an empty String
    public String getClosingSoonTime(Restaurant restaurant){
        Calendar nowCal = Calendar.getInstance();

        for(Restaurant.OpenPeriod period : Objects.requireNonNull(restaurant.getOpeningHours().get(nowCal.get(Calendar.DAY_OF_WEEK) - 1), "Opening hours not found for this day")){
            Calendar openCal = Calendar.getInstance();
            openCal.set(Calendar.HOUR_OF_DAY, period.getOpeningHour());
            openCal.set(Calendar.MINUTE, period.getOpeningMinute());
            openCal.set(Calendar.ZONE_OFFSET, restaurant.getUtcOffset());

            Calendar closeCal = Calendar.getInstance();
            closeCal.set(Calendar.HOUR_OF_DAY, period.getClosingHour());
            closeCal.set(Calendar.MINUTE, period.getClosingMinute());
            closeCal.set(Calendar.ZONE_OFFSET, restaurant.getUtcOffset());

            if( period.getClosingHour() == 0 && period.getClosingMinute() == 0){
                closeCal.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (nowCal.after(openCal) && nowCal.before(closeCal)) {
                nowCal.add(Calendar.HOUR_OF_DAY, 1);

                if(nowCal.after(closeCal)){
                    closeCal.set(Calendar.ZONE_OFFSET, nowCal.getTimeZone().getRawOffset());
                    return DateFormat.getTimeInstance(DateFormat.SHORT).format(closeCal.getTime());
                }
            }
        }
        return "";
    }

    private void determineWorkmatesViewVisibility(Restaurant restaurant) {
        if(restaurant.getInterestedWorkmates().size() > 0){
            restaurant.setWorkmateIvVisibility(View.VISIBLE);
            restaurant.setWorkmateTvVisibility(View.VISIBLE);
        } else {
            restaurant.setWorkmateIvVisibility(View.INVISIBLE);
            restaurant.setWorkmateTvVisibility(View.INVISIBLE);
        }
    }



}
