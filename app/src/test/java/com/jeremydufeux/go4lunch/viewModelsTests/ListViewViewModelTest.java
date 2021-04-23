package com.jeremydufeux.go4lunch.viewModelsTests;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantUseCase;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.ui.fragment.listView.ListViewViewModel;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListViewViewModelTest {
    private final RestaurantUseCase mRestaurantUseCase = mock(RestaurantUseCase.class);
    private final UserDataRepository mUserDataRepository = mock(UserDataRepository.class);
    private ListViewViewModel mViewModel;

    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init(){
        mViewModel = new  ListViewViewModel(mRestaurantUseCase, mUserDataRepository);
        when(mRestaurantUseCase.observeRestaurantDetailsList()).thenReturn(Observable.just(generateRestaurantList()));
        when(mRestaurantUseCase.observeErrors()).thenReturn(Observable.just(new Exception(new Throwable())));
    }

    @Test
    public void test_observeRestaurantList_givenHashMapWithTwoRestaurant(){
        mViewModel.startObservers();
        mViewModel.observeRestaurantList().observeForever(restaurants -> assertEquals(2, restaurants.size()));
    }

    private HashMap<String, Restaurant> generateRestaurantList() {
        Restaurant restaurant1 = new Restaurant("ChIJH274sClwjEcRniBZAsyAtH0");
        Restaurant restaurant2 = new Restaurant("gdsJH2dhfdhdhdr74sCBZAsyAH0");

        HashMap<String, Restaurant> restaurantHashMap = new HashMap<>();
        restaurantHashMap.put(restaurant1.getUId(), restaurant1);
        restaurantHashMap.put(restaurant2.getUId(), restaurant2);
        return restaurantHashMap;
    }
}
