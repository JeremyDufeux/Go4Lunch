package com.jeremydufeux.go4lunch.mappers;

import android.util.Log;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.Calendar;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.jeremydufeux.go4lunch.utils.Utils.isSameDay;

public class WorkmateToDetailsMapper implements Function< Workmate, Workmate>{

    String mRestaurantUId;

    public WorkmateToDetailsMapper(String restaurantUId) {
        mRestaurantUId = restaurantUId;
    }

    @Override
    public Workmate apply(@NonNull Workmate workmate) {

        workmate.setWorkmateGoFabColor(R.color.orange);

        if(workmate.getChosenRestaurantDate() != null){
            Calendar now = Calendar.getInstance();
            Calendar workmateDate = Calendar.getInstance();
            workmateDate.setTime(workmate.getChosenRestaurantDate());
            if(isSameDay(now, workmateDate) && mRestaurantUId.equals(workmate.getChosenRestaurantId())){
                workmate.setWorkmateGoFabColor(R.color.green);
            }
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
