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
    int mDistanceUnit;
    Calendar mNowCal;

    public RestaurantToListViewMapper(Location location, int distanceUnit, Calendar nowCal) {
        mLocation = location;
        mDistanceUnit = distanceUnit;
        mNowCal = nowCal;
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
            if(mDistanceUnit == R.string.unit_feet_short) {
                restaurant.setDistanceFromUser((int) (mLocation.distanceTo(restaurant.getLocation()) * 3.28084f));
                restaurant.setDistanceUnitString(mDistanceUnit);
            } else {
                restaurant.setDistanceFromUser((int) mLocation.distanceTo(restaurant.getLocation()));
                restaurant.setDistanceUnitString(mDistanceUnit);
            }
            restaurant.setDistanceTvVisibility(View.VISIBLE);
        } else {
            restaurant.setDistanceTvVisibility(View.INVISIBLE);
        }
    }

    private void determineOpening(Restaurant restaurant) {
        restaurant.setOpenTvColor(R.color.grey);

        if(restaurant.isOpeningHoursAvailable()) {
            if(restaurant.isAlwaysOpen()){
                restaurant.setOpenTvString(R.string.open_now);
            }
            else {
                restaurant.setOpenTvCloseTimeString(getOpenStatus(restaurant));

                if (!restaurant.getOpenTvCloseTimeString().isEmpty()) {
                    if(restaurant.getOpenTvCloseTimeString().equals("open")){
                        restaurant.setOpenTvString(R.string.open_now);
                    }
                    else if(restaurant.getOpenTvCloseTimeString().equals("closed")){
                        restaurant.setOpenTvString(R.string.closed);
                        restaurant.setOpenTvColor(R.color.red);
                    }
                    else {
                        restaurant.setOpenTvString(R.string.open_until);
                    }
                    restaurant.setOpenTvVisibility(View.VISIBLE);
                } else {
                    restaurant.setOpenTvString(R.string.no_open_hours);
                    restaurant.setOpenTvVisibility(View.INVISIBLE);
                }
            }
        } else {
            restaurant.setOpenTvString(R.string.no_open_hours);
            restaurant.setOpenTvVisibility(View.INVISIBLE);
        }
    }

    // If closing soon, return the closing time, else return an empty String
    public String getOpenStatus(Restaurant restaurant){
        if(restaurant.getOpeningHours().containsKey(mNowCal.get(Calendar.DAY_OF_WEEK) - 1)) {
            for (Restaurant.OpenPeriod period : Objects.requireNonNull(restaurant.getOpeningHours().get(mNowCal.get(Calendar.DAY_OF_WEEK) - 1), "Opening hours not found for this day")) {
                Calendar openCal = (Calendar) mNowCal.clone();
                openCal.set(Calendar.HOUR_OF_DAY, period.getOpeningHour());
                openCal.set(Calendar.MINUTE, period.getOpeningMinute());

                Calendar closeCal = (Calendar) mNowCal.clone();
                closeCal.set(Calendar.HOUR_OF_DAY, period.getClosingHour());
                closeCal.set(Calendar.MINUTE, period.getClosingMinute());

                if (period.getClosingHour() == 0) {
                    closeCal.add(Calendar.DAY_OF_MONTH, 1);
                }

                if (mNowCal.after(openCal) && mNowCal.before(closeCal)) {
                    mNowCal.add(Calendar.HOUR_OF_DAY, 1);

                    if (mNowCal.after(closeCal)) {
                        closeCal.set(Calendar.ZONE_OFFSET, mNowCal.getTimeZone().getRawOffset());
                        return DateFormat.getTimeInstance(DateFormat.SHORT).format(closeCal.getTime());
                    } else {
                        return "open";
                    }
                }
            }
        }
        return "closed";
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
