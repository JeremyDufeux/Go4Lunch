package com.jeremydufeux.go4lunch.mappersTests;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.WorkmateToDetailsMapper;
import com.jeremydufeux.go4lunch.models.Workmate;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class WorkmateToDetailsMapperTest {

    @Test
    public void test_likedRestaurants_givenWorkmateWithNoRestaurantLiked(){
        Workmate workmate = generateWorkmate();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274sClwjEcRniBZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.string.like, mappedWorkmate.getWorkmateLikedRestaurantTvText());
        assertEquals(R.color.orange, mappedWorkmate.getWorkmateLikedRestaurantTvColor());
    }

    @Test
    public void test_likedRestaurants_givenWorkmateWithRestaurantLiked(){
        Workmate workmate = generateWorkmateWithLikedRestaurant();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274sClwjEcRniBZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.string.liked, mappedWorkmate.getWorkmateLikedRestaurantTvText());
        assertEquals(R.color.green, mappedWorkmate.getWorkmateLikedRestaurantTvColor());
    }

    @Test
    public void test_chosenRestaurant_givenWorkmateWithNoChosenRestaurant(){
        Workmate workmate = generateWorkmate();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274sClwjEcRniBZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.color.orange, mappedWorkmate.getWorkmateGoFabColor());
    }

    @Test
    public void test_chosenRestaurant_givenWorkmateWithChosenRestaurant(){
        Workmate workmate = generateWorkmateWithChosenRestaurant();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274sClwjEcRniBZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.color.green, mappedWorkmate.getWorkmateGoFabColor());
    }

    @Test
    public void test_chosenRestaurant_givenWorkmateWithChosenRestaurantLastDay(){
        Workmate workmate = generateWorkmateWithChosenRestaurantLastDay();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274sClwjEcRniBZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.color.orange, mappedWorkmate.getWorkmateGoFabColor());
    }

    @Test
    public void test_chosenRestaurant_givenWorkmateWithChosenAnotherRestaurant(){
        Workmate workmate = generateWorkmateWithChosenRestaurantLastDay();
        WorkmateToDetailsMapper mapper = new WorkmateToDetailsMapper("ChIJH274xhtyqzerZAsyAtH0");

        Workmate mappedWorkmate = mapper.apply(workmate);

        assertEquals(R.color.orange, mappedWorkmate.getWorkmateGoFabColor());
    }

    private Workmate generateWorkmate(){
        return new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg");
    }

    private Workmate generateWorkmateWithLikedRestaurant(){

        Workmate workmate = new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg");

        workmate.getLikedRestaurants().add("ChIJH274sClwjEcRniBZAsyAtH0");

        return workmate;
    }

    private Workmate generateWorkmateWithChosenRestaurant(){

        Workmate workmate = new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg");

        workmate.setChosenRestaurantId("ChIJH274sClwjEcRniBZAsyAtH0");
        workmate.setChosenRestaurantName("Le viand'art");
        workmate.setChosenRestaurantDate(Calendar.getInstance().getTime());

        return workmate;
    }

    private Workmate generateWorkmateWithChosenRestaurantLastDay(){

        Workmate workmate = new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg");

        workmate.setChosenRestaurantId("ChIJH274sClwjEcRniBZAsyAtH0");
        workmate.setChosenRestaurantName("Le viand'art");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        workmate.setChosenRestaurantDate(calendar.getTime());

        return workmate;
    }
}
