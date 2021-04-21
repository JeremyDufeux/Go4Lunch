package com.jeremydufeux.go4lunch.repositoriesTests;

import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.service.FakePlaceServiceProvider;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GooglePlacesRepositoryJsonTest {

    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    GooglePlacesRepository mGooglePlacesRepository;

    @Before
    public void getNewPlaceRepository() {
        mGooglePlacesRepository = new GooglePlacesRepository(FakePlaceServiceProvider.provideService());
    }

    @Test
    public void test_getNearbyPlaces_sizeAndFirstPlaceName(){
        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results ->
                        results.getPlaceSearches().size() == 20
                        && results.getPlaceSearches().get(0).getName().equals("Perret Laurent"))
                .dispose();
    }

    @Test
    public void test_fetchNextPageNearbyPlaces_size(){
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

    @Test
    public void test_haveNextPageToken(){
        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results ->
                        results.getPlaceSearches().size() == 20)
                .dispose();

        assertTrue(mGooglePlacesRepository.haveNextPageToken());

        mGooglePlacesRepository.getNextPageNearbyPlaces()
                .test()
                .assertValue(results ->
                        (results.getPlaceSearches().size() == 4))
                .dispose();

        assertFalse(mGooglePlacesRepository.haveNextPageToken());
    }

    @Test
    public void test_getPlaceDetails_placeData(){
        mGooglePlacesRepository.getDetailsForPlaceId("ChIJH274sClwjEcRniBZAsyAtH0")
                .test()
                .assertValue(results -> {
                    PlaceDetails placeDetails = results.getPlaceDetails();
                    return placeDetails.getName().equals("Le viand'art")
                            && placeDetails.getPlaceId().equals("ChIJH274sClwjEcRniBZAsyAtH0")
                            && placeDetails.getPlaceDetailsGeometry().getLocation().getLat() == 46.19184599999999
                            && placeDetails.getPlaceDetailsGeometry().getLocation().getLng() == 6.220219999999999
                            && placeDetails.getVicinity().equals("56 Rue de Genève, Ambilly")
                            && placeDetails.getOpeningHours() != null
                            && placeDetails.getUtcOffset() == 120
                            && placeDetails.getPhotos().get(0).getPhotoReference().equals("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp")
                            && placeDetails.getInternationalPhoneNumber().equals("+33 4 50 92 80 69")
                            && placeDetails.getWebsite().equals("https://le-viandart.business.site/")
                            && placeDetails.getRating() == 4.2f;
                })
                .dispose();
    }

    @Test
    public void test_getAutocompletePlaces(){
        mGooglePlacesRepository.getAutocompletePlaces("", 0, 0, 0)
                .test()
                .assertValue(results ->
                        (results.getPredictions().size() == 5)
                                && (results.getPredictions().get(0).getDescription().equals("Burger King, Route de Livron, Annemasse, France")))
                .dispose();
    }
}
