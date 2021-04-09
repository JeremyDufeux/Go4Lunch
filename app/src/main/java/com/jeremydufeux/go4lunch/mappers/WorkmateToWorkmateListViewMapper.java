package com.jeremydufeux.go4lunch.mappers;

import android.view.View;

import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.jeremydufeux.go4lunch.utils.Utils.isToday;

public class WorkmateToWorkmateListViewMapper implements Function<List<Workmate>, List<Workmate>> {
    @Override
    public List<Workmate> apply(@NonNull List<Workmate> workmateList) {
        for (Workmate workmate : workmateList){
            if(workmate.getChosenRestaurantDate() != null
                    && !workmate.getChosenRestaurantId().isEmpty()
                        && !workmate.getChosenRestaurantName().isEmpty()){
                if(isToday(workmate.getChosenRestaurantDate())){
                    workmate.setWorkmateChosenTvVisibility(View.VISIBLE);
                    workmate.setWorkmateNotChosenTvVisibility(View.INVISIBLE);
                } else {
                    setNotDecidedYet(workmate);
                }
            } else {
                setNotDecidedYet(workmate);
            }
        }
        Collections.sort(workmateList);
        return workmateList;
    }

    private void setNotDecidedYet(Workmate workmate){
        workmate.setWorkmateChosenTvVisibility(View.INVISIBLE);
        workmate.setWorkmateNotChosenTvVisibility(View.VISIBLE);
    }
}


