package com.jeremydufeux.go4lunch.mappers;

import android.view.View;

import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.Calendar;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class WorkmateMapper implements Function<List<Workmate>, List<Workmate>> {
    @Override
    public List<Workmate> apply(@NonNull List<Workmate> workmateList) {
        for (Workmate workmate : workmateList){
            if(workmate.getLastChosenRestaurantDate() != null){
                Calendar now = Calendar.getInstance();
                Calendar workmateDate = Calendar.getInstance();
                workmateDate.setTime(workmate.getLastChosenRestaurantDate().toDate());
                if(isSameDay(now, workmateDate)){
                    workmate.setWorkmateChosenTvVisibility(View.VISIBLE);
                    workmate.setWorkmateNotChosenTvVisibility(View.INVISIBLE);
                } else {
                    setNotDecidedYet(workmate);
                }
            } else {
                setNotDecidedYet(workmate);
            }
        }
        return workmateList;
    }

    private void setNotDecidedYet(Workmate workmate){
        workmate.setWorkmateChosenTvVisibility(View.INVISIBLE);
        workmate.setWorkmateNotChosenTvVisibility(View.VISIBLE);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}


