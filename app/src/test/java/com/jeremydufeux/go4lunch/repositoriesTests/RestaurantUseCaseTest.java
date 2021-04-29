package com.jeremydufeux.go4lunch.repositoriesTests;

import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
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
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantUseCase;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.RxJavaSchedulersTestRule2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;

import static com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultToRestaurantMapper.OPERATIONAL_BUSINESS_STATUS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class RestaurantUseCaseTest {

    @Rule
    public RxJavaSchedulersTestRule2 rxRule = new RxJavaSchedulersTestRule2();

    RestaurantUseCase mRestaurantUseCase;
    PlacesService mPlacesService = Mockito.mock(PlacesService.class);
    WorkmatesRepository mWorkmatesRepository = Mockito.mock(WorkmatesRepository.class);

    PlaceSearchResults mFirstPlaceSearchResults = generateNearbySearchFirstResults();
    PlaceSearchResults mSecondPlaceSearchResults = generateNearbySearchSecondResults();
    PlaceDetailsResults mFirstDetailsResults = generatePlaceDetailsResultsForFirstPlace();
    PlaceDetailsResults mSecondDetailsResults = generatePlaceDetailsResultsForSecondPlace();
    PlaceDetailsResults mThirdDetailsResults = generatePlaceDetailsResultsForThirdPlace();

    List<Workmate> mInterestedWorkmates = generateWorkmateList();

    String mFirstPlaceId = mFirstDetailsResults.getPlaceDetails().getPlaceId();
    String mSecondPlaceId = mSecondDetailsResults.getPlaceDetails().getPlaceId();
    String mThirdPlaceId = mThirdDetailsResults.getPlaceDetails().getPlaceId();

    @Before
    public void initRestaurantUseCase() {
        mRestaurantUseCase = new RestaurantUseCase(
                new GooglePlacesRepository(mPlacesService),
                new RestaurantRepository(),
                mWorkmatesRepository);

        when(mPlacesService.fetchNearbyPlaces(any(), any(), any(), any())).thenReturn(Observable.just(mFirstPlaceSearchResults));
        when(mPlacesService.fetchNextPageNearbyPlaces(any(), any())).thenReturn(Observable.just(mSecondPlaceSearchResults));
        when(mPlacesService.fetchDetailsForPlaceId(any(), any(), eq(mFirstPlaceId), any()))
                .thenReturn(Observable.just(mFirstDetailsResults));
        when(mPlacesService.fetchDetailsForPlaceId(any(), any(), eq(mSecondPlaceId), any()))
                .thenReturn(Observable.just(mSecondDetailsResults));
        when(mPlacesService.fetchDetailsForPlaceId(any(), any(), eq(mThirdPlaceId), any()))
                .thenReturn(Observable.just(mThirdDetailsResults));
        when(mWorkmatesRepository.getInterestedWorkmatesForRestaurants(eq(mFirstPlaceId))).thenReturn(Observable.just(mInterestedWorkmates));
        when(mWorkmatesRepository.getInterestedWorkmatesForRestaurants(AdditionalMatchers.not(eq(mFirstPlaceId)))).thenReturn(Observable.just(new ArrayList<>()));
    }

    @Test
    public void test_getNearbyPlaces_checkRestaurantList(){
        mRestaurantUseCase.getNearbyPlaces(0, 0, 0);

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap ->
                        restaurantHashMap.size() == mFirstPlaceSearchResults.getPlaceSearches().size()
                        && restaurantHashMap.containsKey(mFirstPlaceId)
                        && Objects.requireNonNull(restaurantHashMap.get(mSecondPlaceId)).getName().equals(mSecondDetailsResults.getPlaceDetails().getName())
                        && Objects.requireNonNull(restaurantHashMap.get(mSecondPlaceId)).getUtcOffset() == mSecondDetailsResults.getPlaceDetails().getUtcOffset()*60000);
    }

    @Test
    public void test_loadNextPage_checkNewListSizeAndNewPlaceName(){
        mRestaurantUseCase.getNearbyPlaces(0, 0, 0);

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap ->
                        restaurantHashMap.size() == mFirstPlaceSearchResults.getPlaceSearches().size());

        mRestaurantUseCase.loadNextPage();

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap ->
                        restaurantHashMap.size() == mFirstPlaceSearchResults.getPlaceSearches().size() + mSecondPlaceSearchResults.getPlaceSearches().size()
                && Objects.requireNonNull(restaurantHashMap.get(mThirdPlaceId)).getName().equals(mThirdDetailsResults.getPlaceDetails().getName()));
    }

    @Test
    public void test_getNearbyPlace_withMinimumPlaceDetails(){
        PlaceSearchResults placeSearchResults = generateNearbySearchResultsForMinimumDataCheck();
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsResultsWithMinimumData();
        String placeId = placeDetailsResults.getPlaceDetails().getPlaceId();

        when(mPlacesService.fetchNearbyPlaces(any(), any(), any(), any())).thenReturn(Observable.just(placeSearchResults));
        when(mPlacesService.fetchDetailsForPlaceId(any(), any(), eq(placeDetailsResults.getPlaceDetails().getPlaceId()), any()))
                .thenReturn(Observable.just(placeDetailsResults));

        mRestaurantUseCase.getNearbyPlaces(0, 0, 0);

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap -> restaurantHashMap.containsKey(placeId));

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap -> Objects.requireNonNull(restaurantHashMap.get(placeId)).getName().equals("Bfc Tandoori"));
    }

    @Test
    public void test_observeRestaurantList_checkInterestedWorkmatesWhenTwoInterested(){
        mRestaurantUseCase.getNearbyPlaces(0, 0, 0);

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap -> restaurantHashMap.get(mFirstPlaceId).getInterestedWorkmates().size() == 2
                        && restaurantHashMap.get(mFirstPlaceId).getInterestedWorkmates().contains(mInterestedWorkmates.get(0).getUId()));
    }

    @Test
    public void test_observeRestaurantList_checkInterestedWorkmatesWhenNoInterested(){
        mRestaurantUseCase.getNearbyPlaces(0, 0, 0);

        mRestaurantUseCase.observeRestaurantList()
                .test()
                .assertValue(restaurantHashMap -> restaurantHashMap.get(mSecondPlaceId).getInterestedWorkmates().size() == 0);
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

    private PlaceDetailsResults generatePlaceDetailsResultsForSecondPlace(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");
        placeDetails.setVicinity("62Bis Rue de Genève, Rue des Rosiers, Gaillard");
        placeDetails.setUtcOffset(120);
        placeDetails.setInternationalPhoneNumber("+33 4 50 38 86 23");
        placeDetails.setWebsite("http://www.mizuki-sushi.fr/");
        placeDetails.setRating(4.5f);

        placeDetails.setPlaceDetailsGeometry(new PlaceDetailsGeometry());
        placeDetails.getPlaceDetailsGeometry().setLocation(new Location());
        placeDetails.getPlaceDetailsGeometry().getLocation().setLat(46.1918519);
        placeDetails.getPlaceDetailsGeometry().getLocation().setLng(6.219075699999999);

        placeDetails.setAddressComponents(new ArrayList<>());
        AddressComponent streetNumber = new AddressComponent();
        streetNumber.setTypes(Collections.singletonList("street_number"));
        streetNumber.setLongName("62Bis");
        AddressComponent route = new AddressComponent();
        route.setTypes(Collections.singletonList("route"));
        route.setLongName("Rue des Rosiers");
        placeDetails.getAddressComponents().add(streetNumber);
        placeDetails.getAddressComponents().add(route);

        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
            Period morningPeriod = new Period();

            morningPeriod.setOpen(new Open());
            morningPeriod.getOpen().setDay(i);
            morningPeriod.getOpen().setTime("1130");

            morningPeriod.setClose(new Close());
            morningPeriod.getClose().setDay(i);
            morningPeriod.getClose().setTime("1400");

            placeDetails.getOpeningHours().getPeriods().add(morningPeriod);

            Period afternoonPeriod = new Period();

            afternoonPeriod.setOpen(new Open());
            afternoonPeriod.getOpen().setDay(i);
            afternoonPeriod.getOpen().setTime("1800");

            afternoonPeriod.setClose(new Close());
            afternoonPeriod.getClose().setDay(i);
            afternoonPeriod.getClose().setTime("2130");

            placeDetails.getOpeningHours().getPeriods().add(afternoonPeriod);
        }

        Photo photo = new Photo();
        photo.setPhotoReference("ATtYBwIwRL1YDi_-zQe8-9Gjfl54WdTG5ycRVlJxq8XrSTqOjFKXgK3mrG43hgE1NU9X3amiqk0ImeJw7gw8lQdY6fd5MNi6llEWXqmDSeBV6PwJ3dlXs8gqyENezYGiGRXWSk7dtj0R90QoLxsAQSYcP7EUw9Ym-NE2i6purSbZ203KtyWL");

        placeDetails.setPhotos(new ArrayList<>());
        placeDetails.getPhotos().add(photo);

        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }

    private PlaceDetailsResults generatePlaceDetailsResultsForThirdPlace(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Lunch Time");
        placeDetails.setPlaceId("ChIJIaGT4CZwjEcRkrq89YvQaBA");
        placeDetails.setVicinity("117 Rue de Genève, Gaillard");
        placeDetails.setUtcOffset(120);
        placeDetails.setInternationalPhoneNumber("+33 4 50 92 80 69");
        placeDetails.setWebsite("https://le-viandart.business.site/");
        placeDetails.setRating(4.2f);

        placeDetails.setPlaceDetailsGeometry(new PlaceDetailsGeometry());
        placeDetails.getPlaceDetailsGeometry().setLocation(new Location());
        placeDetails.getPlaceDetailsGeometry().getLocation().setLat(46.1913187);
        placeDetails.getPlaceDetailsGeometry().getLocation().setLng(6.2125187);

        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }

    private PlaceSearchResults generateNearbySearchResultsForMinimumDataCheck() {
        PlaceSearch placeSearch1 = new PlaceSearch();
        placeSearch1.setName("Bfc Tandoori");
        placeSearch1.setPlaceId("ChIJmUrmuilwjEcRsFPkJ2VwOuM-4k");
        placeSearch1.setPlaceSearchGeometry(new PlaceSearchGeometry());
        placeSearch1.getPlaceSearchGeometry().setLocation(new Location());
        placeSearch1.getPlaceSearchGeometry().getLocation().setLat(46.1917389);
        placeSearch1.getPlaceSearchGeometry().getLocation().setLng(6.220750999999999);
        placeSearch1.setBusinessStatus(OPERATIONAL_BUSINESS_STATUS);

        List<PlaceSearch> placeSearchList = new ArrayList<>();
        placeSearchList.add(placeSearch1);

        PlaceSearchResults placeSearchResults = new PlaceSearchResults();
        placeSearchResults.setPlaceSearches(placeSearchList);

        return placeSearchResults;
    }

    private PlaceDetailsResults generatePlaceDetailsResultsWithMinimumData() {
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Bfc Tandoori");
        placeDetails.setPlaceId("ChIJmUrmuilwjEcRsFPkJ2VwOuM-4k");

        placeDetails.setPlaceDetailsGeometry(new PlaceDetailsGeometry());
        placeDetails.getPlaceDetailsGeometry().setLocation(new Location());
        placeDetails.getPlaceDetailsGeometry().getLocation().setLat(46.1917389);
        placeDetails.getPlaceDetailsGeometry().getLocation().setLng(6.220750999999999);

        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }

    private List<Workmate> generateWorkmateList(){
        List<Workmate> workmateList = new ArrayList<>();

        workmateList.add(new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "John Doe",
                "John",
                "john.doe@gmail.com",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/John_Doe%2C_born_John_Nommensen_Duchac.jpg/260px-John_Doe%2C_born_John_Nommensen_Duchac.jpg"));

        workmateList.add(new Workmate("Db5e374sClwjEbqoF8ZAsyAtH0",
                "Jane Doe",
                "Jane",
                "Jane.doe@gmail.com",
                "https://fr.web.img6.acsta.net/pictures/19/08/19/12/15/0898799.jpg"));

        return workmateList;
    }
}
