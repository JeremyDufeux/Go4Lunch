package com.jeremydufeux.go4lunch.mappers;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class WorkmateToDetailsMapper implements Function< Workmate, Workmate>{

    String mRestaurantUId;

    public WorkmateToDetailsMapper(String restaurantUId) {
        mRestaurantUId = restaurantUId;
    }

    @Override
    public Workmate apply(@NonNull Workmate workmate) {
        if(mRestaurantUId.equals(workmate.getChosenRestaurantId())){
            workmate.setWorkmateGoFabColor(R.color.green);
        } else {
            workmate.setWorkmateGoFabColor(R.color.orange);
        }

        if(workmate.getLikedRestaurants().contains(mRestaurantUId)){
            workmate.setWorkmateLikedRestaurantTvText(R.string.liked);
            workmate.setWorkmateLikedRestaurantTvColor(R.color.green);
        } else {
            workmate.setWorkmateLikedRestaurantTvText(R.string.like);
            workmate.setWorkmateLikedRestaurantTvColor(R.color.orange);
        }

        return workmate;
    }
}
