package com.jeremydufeux.go4lunch;

import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.service.FakePlaceServiceProvider;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Rule;
import org.junit.Test;

public class GooglePlacesRepositoryTest {

    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    public GooglePlacesRepository getNewPlaceRepository() {
        return new GooglePlacesRepository(FakePlaceServiceProvider.provideService());
    }

    @Test
    public void test_getNearbyPlaces_size(){
        GooglePlacesRepository mGooglePlacesRepository = getNewPlaceRepository();

        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results ->
                        results.getPlaceSearches().size() == 20)
                .dispose();
    }

    @Test
    public void test_getNearbyPlaces_placeName(){
        GooglePlacesRepository mGooglePlacesRepository = getNewPlaceRepository();

        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results -> results.getPlaceSearches().get(0).getName().equals("Perret Laurent"))
                .dispose();
    }

    @Test
    public void test_fetchNextPageNearbyPlaces_size(){
        GooglePlacesRepository mGooglePlacesRepository = getNewPlaceRepository();

        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results ->
                        results.getPlaceSearches().size() == 20)
                .dispose();

        mGooglePlacesRepository.getNextPageNearbyPlaces()
                .test()
                .assertValue(results ->
                        (results.getPlaceSearches().size() == 4)
                                && (results.getPlaceSearches().get(0).getName().equals("Café - Restaurant De L´acacia")))
                .dispose();
    }
}
