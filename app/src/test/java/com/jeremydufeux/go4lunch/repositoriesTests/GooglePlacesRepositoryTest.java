package com.jeremydufeux.go4lunch.repositoriesTests;

import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.PlaceAutocomplete;
import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.Prediction;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Close;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsGeometry;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Open;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.OpeningHours;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Photo;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.Location;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchGeometry;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;

import static com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultToRestaurantMapper.OPERATIONAL_BUSINESS_STATUS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GooglePlacesRepositoryTest {

    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    GooglePlacesRepository mGooglePlacesRepository;
    PlacesService mPlacesService = Mockito.mock(PlacesService.class);

    @Before
    public void initGooglePlaceRepository(){
        mGooglePlacesRepository = new GooglePlacesRepository(mPlacesService);
    }

    @Test
    public void test_getNearbyPlaces_checkSizeAndFirstPlaceName(){
        PlaceSearchResults result = generateNearbySearchFirstResults();
        when(mPlacesService.fetchNearbyPlaces(any(), any(), any(), any())).thenReturn(Observable.just(result));

        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results -> {
                    List<PlaceSearch> placesSearchList = results.getPlaceSearches();
                    return results.getPlaceSearches().size() == 2
                            && placesSearchList.get(0).getName().equals("Le viand'art")
                            && placesSearchList.get(0).getPlaceId().equals("ChIJH274sClwjEcRniBZAsyAtH0")
                            && placesSearchList.get(1).getName().equals("Mizuki")
                            && placesSearchList.get(1).getPlaceId().equals("ChIJmZKgsilwjEcRKVdZd_cE-4k");
                })
                .dispose();
    }

    @Test
    public void test_getPlaceDetails_checkPlaceData() {
        PlaceDetailsResults mockedResults = generatePlaceDetailsResultsForFirstPlace();
        when(mPlacesService.fetchDetailsForPlaceId(any(), any(), any(), any())).thenReturn(Observable.just(mockedResults));

        mGooglePlacesRepository.getDetailsForPlaceId("")
                .test()
                .assertValue(results -> {
                    PlaceDetails placeDetails = results.getPlaceDetails();
                    return placeDetails.getName().equals("Le viand'art")
                            && placeDetails.getPlaceId().equals("ChIJH274sClwjEcRniBZAsyAtH0")
                            && placeDetails.getVicinity().equals("56 Rue de Genève, Ambilly")
                            && placeDetails.getUtcOffset() == 120
                            && placeDetails.getInternationalPhoneNumber().equals("+33 4 50 92 80 69")
                            && placeDetails.getWebsite().equals("https://le-viandart.business.site/")
                            && placeDetails.getRating() == 4.2f
                            && placeDetails.getPlaceDetailsGeometry().getLocation().getLat() == 46.19184599999999
                            && placeDetails.getPlaceDetailsGeometry().getLocation().getLng() == 6.220219999999999
                            && placeDetails.getOpeningHours().getPeriods().size() == 7
                            && placeDetails.getOpeningHours().getPeriods().get(0).getOpen().getDay() == 0
                            && placeDetails.getOpeningHours().getPeriods().get(0).getOpen().getTime().equals("1000")
                            && placeDetails.getOpeningHours().getPeriods().get(1).getClose().getDay() == 1
                            && placeDetails.getOpeningHours().getPeriods().get(1).getClose().getTime().equals("2000")
                            && placeDetails.getPhotos().get(0).getPhotoReference().equals("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp")
                            ;
                })
                .dispose();
    }

    @Test
    public void test_haveNextPageToken_checkToken(){
        PlaceSearchResults mockedFirstResults = generateNearbySearchFirstResults();
        when(mPlacesService.fetchNearbyPlaces(any(), any(), any(), any())).thenReturn(Observable.just(mockedFirstResults));

        mGooglePlacesRepository.getNearbyPlaces(0, 0,0)
                .test()
                .assertValue(results -> results.getPlaceSearches().size() == 2)
                .dispose();

        assertTrue(mGooglePlacesRepository.haveNextPageToken());

        PlaceSearchResults mockedSecondResults = generateNearbySearchSecondResults();
        when(mPlacesService.fetchNextPageNearbyPlaces(any(), any())).thenReturn(Observable.just(mockedSecondResults));

        mGooglePlacesRepository.getNextPageNearbyPlaces()
                .test()
                .assertValue(results -> results.getPlaceSearches().size() == 1)
                .dispose();

        assertFalse(mGooglePlacesRepository.haveNextPageToken());
    }

    @Test
    public void test_getAutocompletePlaces_checkPredictionAmount(){
        PlaceAutocomplete autocompleteResult = generateAutocompleteResults();
        when(mPlacesService.fetchPlaceAutocomplete(any(), any(), any(), any(), any(), any())).thenReturn(Observable.just(autocompleteResult));

        mGooglePlacesRepository.getAutocompletePlaces("", 0,0,0)
                .test()
                .assertValue(results -> results.getPredictions().size() == 3)
                .dispose();
    }

    // ---------------
    // Generate data
    // ---------------

    private PlaceSearchResults generateNearbySearchFirstResults() {
        PlaceSearch placeSearch1 = new PlaceSearch();
        placeSearch1.setName("Le viand'art");
        placeSearch1.setPlaceId("ChIJH274sClwjEcRniBZAsyAtH0");
        placeSearch1.setPlaceSearchGeometry(new PlaceSearchGeometry());
        placeSearch1.getPlaceSearchGeometry().setLocation(new Location());
        placeSearch1.getPlaceSearchGeometry().getLocation().setLat(46.19184599999999);
        placeSearch1.getPlaceSearchGeometry().getLocation().setLng(6.220219999999999);
        placeSearch1.setBusinessStatus(OPERATIONAL_BUSINESS_STATUS);

        PlaceSearch placeSearch2 = new PlaceSearch();
        placeSearch2.setName("Mizuki");
        placeSearch2.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");
        placeSearch2.setPlaceSearchGeometry(new PlaceSearchGeometry());
        placeSearch2.getPlaceSearchGeometry().setLocation(new Location());
        placeSearch2.getPlaceSearchGeometry().getLocation().setLat(46.1918519);
        placeSearch2.getPlaceSearchGeometry().getLocation().setLng(6.219075699999999);
        placeSearch2.setBusinessStatus(OPERATIONAL_BUSINESS_STATUS);

        List<PlaceSearch> placeSearchList = new ArrayList<>();
        placeSearchList.add(placeSearch1);
        placeSearchList.add(placeSearch2);

        PlaceSearchResults placeSearchResults = new PlaceSearchResults();
        placeSearchResults.setPlaceSearches(placeSearchList);
        placeSearchResults.setNextPageToken("nextPageToken");
        placeSearchResults.setStatus("OK");

        return placeSearchResults;
    }

    private PlaceSearchResults generateNearbySearchSecondResults() {
        PlaceSearch placeSearch1 = new PlaceSearch();
        placeSearch1.setName("Lunch Time");
        placeSearch1.setPlaceId("ChIJIaGT4CZwjEcRkrq89YvQaBA");
        placeSearch1.setPlaceSearchGeometry(new PlaceSearchGeometry());
        placeSearch1.getPlaceSearchGeometry().setLocation(new Location());
        placeSearch1.getPlaceSearchGeometry().getLocation().setLat(46.1913187);
        placeSearch1.getPlaceSearchGeometry().getLocation().setLng(6.2125187);
        placeSearch1.setBusinessStatus(OPERATIONAL_BUSINESS_STATUS);

        List<PlaceSearch> placeSearchList = new ArrayList<>();
        placeSearchList.add(placeSearch1);

        PlaceSearchResults placeSearchResults = new PlaceSearchResults();
        placeSearchResults.setPlaceSearches(placeSearchList);
        placeSearchResults.setStatus("OK");

        return placeSearchResults;
    }

    private PlaceDetailsResults generatePlaceDetailsResultsForFirstPlace(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Le viand'art");
        placeDetails.setPlaceId("ChIJH274sClwjEcRniBZAsyAtH0");
        placeDetails.setVicinity("56 Rue de Genève, Ambilly");
        placeDetails.setUtcOffset(120);
        placeDetails.setInternationalPhoneNumber("+33 4 50 92 80 69");
        placeDetails.setWebsite("https://le-viandart.business.site/");
        placeDetails.setRating(4.2f);

        placeDetails.setPlaceDetailsGeometry(new PlaceDetailsGeometry());
        placeDetails.getPlaceDetailsGeometry().setLocation(new Location());
        placeDetails.getPlaceDetailsGeometry().getLocation().setLat(46.19184599999999);
        placeDetails.getPlaceDetailsGeometry().getLocation().setLng(6.220219999999999);

        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
            Period period = new Period();

            period.setOpen(new Open());
            period.getOpen().setDay(i);
            period.getOpen().setTime("1000");

            period.setClose(new Close());
            period.getClose().setDay(i);
            period.getClose().setTime("2000");

            placeDetails.getOpeningHours().getPeriods().add(period);
        }

        Photo photo = new Photo();
        photo.setPhotoReference("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp");

        placeDetails.setPhotos(new ArrayList<>());
        placeDetails.getPhotos().add(photo);

        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }

    private PlaceAutocomplete generateAutocompleteResults(){
        PlaceAutocomplete autocompleteResult = new PlaceAutocomplete();

        Prediction prediction1 = new Prediction();
        prediction1.setDescription("Burger King, Route de Livron, Annemasse, France");
        Prediction prediction2 = new Prediction();
        prediction2.setDescription("Burger n Tacos, Rue Albert Montfort, Annemasse, France");
        Prediction prediction3 = new Prediction();
        prediction3.setDescription("Bureau de tabac, Place de l'Étoile, Annemasse, France");

        autocompleteResult.setPredictions(new ArrayList<>());
        autocompleteResult.getPredictions().add(prediction1);
        autocompleteResult.getPredictions().add(prediction2);
        autocompleteResult.getPredictions().add(prediction3);
        autocompleteResult.setStatus("OK");

        return autocompleteResult;
    }
}
