package com.jeremydufeux.go4lunch.mappersTests;

import android.view.View;

import com.jeremydufeux.go4lunch.mappers.WorkmateToWorkmateListViewMapper;
import com.jeremydufeux.go4lunch.models.Workmate;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WorkmateToWorkmateListViewMapperTest {

    @Test
    public void test_chosenRestaurantVisibility_givenWorkmateWithNoChosenRestaurant(){
        List<Workmate> workmateList = generateWorkmateList();
        WorkmateToWorkmateListViewMapper mapper = new WorkmateToWorkmateListViewMapper();

        List<Workmate> mappedWorkmateList = mapper.apply(workmateList);

        assertEquals(View.INVISIBLE, mappedWorkmateList.get(0).getWorkmateChosenTvVisibility());
        assertEquals(View.VISIBLE, mappedWorkmateList.get(0).getWorkmateNotChosenTvVisibility());
    }

    @Test
    public void test_chosenRestaurantVisibility_givenWorkmateWithChosenRestaurant(){
        List<Workmate> workmateList = generateWorkmateListWithChosenRestaurant();
        WorkmateToWorkmateListViewMapper mapper = new WorkmateToWorkmateListViewMapper();

        List<Workmate> mappedWorkmateList = mapper.apply(workmateList);

        assertEquals(View.VISIBLE, mappedWorkmateList.get(0).getWorkmateChosenTvVisibility());
        assertEquals(View.INVISIBLE, mappedWorkmateList.get(0).getWorkmateNotChosenTvVisibility());
    }

    private List<Workmate> generateWorkmateList(){
        List<Workmate> workmateList = new ArrayList<>();

        workmateList.add(new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg"));

        return workmateList;
    }

    private List<Workmate> generateWorkmateListWithChosenRestaurant(){
        List<Workmate> workmateList = new ArrayList<>();

        Workmate workmate = new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg");

        workmate.setChosenRestaurantId("ChIJH274sClwjEcRniBZAsyAtH0");
        workmate.setChosenRestaurantName("Le viand'art");
        workmate.setChosenRestaurantDate(Calendar.getInstance().getTime());

        workmateList.add(workmate);

        return workmateList;
    }
}
