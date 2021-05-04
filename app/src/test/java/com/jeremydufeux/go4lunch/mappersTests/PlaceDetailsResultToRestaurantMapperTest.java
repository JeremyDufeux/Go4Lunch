package com.jeremydufeux.go4lunch.mappersTests;

import android.view.View;

import com.jeremydufeux.go4lunch.mappers.PlaceDetailsResultToRestaurantMapper;
import com.jeremydufeux.go4lunch.models.OpenPeriod;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Close;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Open;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.OpeningHours;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Photo;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsGeometry;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaceDetailsResultToRestaurantMapperTest {

    @Test
    public void test_basicValuesMappedCorrectly(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsResultsForFirstPlace();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getName(), placeDetailsResults.getPlaceDetails().getName());
        assertEquals(restaurant.getPhoneNumber(), placeDetailsResults.getPlaceDetails().getInternationalPhoneNumber());
        assertEquals(restaurant.getWebsite(), placeDetailsResults.getPlaceDetails().getWebsite());
    }

    @Test
    public void test_address_givenAddressComponents(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithAddressComponent();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);
        String expectedAddress = "62Bis, Rue des Rosiers";

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getAddress(), expectedAddress);
    }

    @Test
    public void test_address_givenRouteAddressComponents(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRouteAddressComponent();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);
        String expectedAddress = "Rue des Rosiers";

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getAddress(), expectedAddress);
    }

    @Test
    public void test_address_givenVicinity(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithVicinity();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);
        String expectedAddress = "62Bis, Rue des Rosiers";

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getAddress(), expectedAddress);
    }

    @Test
    public void test_photoUrl_givenPhotoRef(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithPhotoRef();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.getPhotoUrl().contains("https://maps.googleapis.com/maps/api/place/photo?photoreference"));
    }

    @Test
    public void test_photoUrl_givenNoPhotoRef(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithNoPhotoRef();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.getPhotoUrl().contains("https://maps.geoapify.com/v1/staticmap"));
    }

    @Test
    public void test_rating_given1(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRating(1);
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getStar1IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar2IvVisibility(), View.GONE);
        assertEquals(restaurant.getStar3IvVisibility(), View.GONE);
    }

    @Test
    public void test_rating_given2(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRating(2);
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getStar1IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar2IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar3IvVisibility(), View.GONE);
    }

    @Test
    public void test_rating_given3(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRating(3);
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getStar1IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar2IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar3IvVisibility(), View.GONE);
    }

    @Test
    public void test_rating_given4(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRating(4);
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getStar1IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar2IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar3IvVisibility(), View.VISIBLE);
    }

    @Test
    public void test_rating_given5(){
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithRating(5);
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getStar1IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar2IvVisibility(), View.VISIBLE);
        assertEquals(restaurant.getStar3IvVisibility(), View.VISIBLE);
    }

    @Test
    public void test_OpeningData_givenOneOpeningPeriodByDay() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithOneOpeningPeriodByDay();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.isOpeningHoursAvailable());
        assertFalse(restaurant.isAlwaysOpen());
        assertEquals(120*60000, restaurant.getUtcOffset());
        for(OpenPeriod period : restaurant.getOpeningPeriods()) {
            if(period.getOpeningDay() == Calendar.SUNDAY) {
                assertEquals(11, period.getOpeningHour());
                assertEquals(30, period.getOpeningMinute());
                assertEquals(18, period.getClosingHour());
                assertEquals(0, period.getClosingMinute());
            } else if(period.getOpeningDay() == Calendar.THURSDAY) {
                assertEquals(11, period.getOpeningHour());
                assertEquals(30, period.getOpeningMinute());
                assertEquals(18, period.getClosingHour());
                assertEquals(0, period.getClosingMinute());
            }
        }
    }

    @Test
    public void test_OpeningData_givenTwoOpeningPeriodByDayExceptSunday() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithTwoOpeningPeriodByDayExceptSunday();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.isOpeningHoursAvailable());
        assertFalse(restaurant.isAlwaysOpen());
        assertEquals(120*60000, restaurant.getUtcOffset());

        boolean sundayIsPresent = false;
        for(OpenPeriod period : restaurant.getOpeningPeriods()) {
            if(period.getOpeningDay() == Calendar.THURSDAY) {
                assertTrue(period.getOpeningHour() == 11 || period.getOpeningHour() == 18);
                assertTrue(period.getOpeningMinute() == 30 || period.getOpeningMinute() == 0);
                assertTrue(period.getClosingHour() == 14 || period.getClosingHour() == 21);
                assertTrue(period.getClosingMinute() == 0 || period.getClosingMinute() == 30);
            }
            if(period.getOpeningDay() == Calendar.SUNDAY) {
                sundayIsPresent = true;
            }
        }
        assertFalse(sundayIsPresent);
    }

    @Test
    public void test_OpeningData_givenCloseAtMidnight() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithOpeningPeriodCloseAtMidnight();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.isOpeningHoursAvailable());
        assertFalse(restaurant.isAlwaysOpen());
        assertEquals(120*60000, restaurant.getUtcOffset());

        for(OpenPeriod period : restaurant.getOpeningPeriods()) {
            if(period.getOpeningDay() == Calendar.MONDAY) {
                assertEquals(8, period.getOpeningHour());
                assertEquals(0, period.getOpeningMinute());
                assertEquals(0, period.getClosingHour());
                assertEquals(0, period.getClosingMinute());
            }
        }
    }

    @Test
    public void test_OpeningData_givenCloseAfterMidnight() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithOpeningPeriodCloseAfterMidnight();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.isOpeningHoursAvailable());
        assertFalse(restaurant.isAlwaysOpen());
        assertEquals(120*60000, restaurant.getUtcOffset());

        for(OpenPeriod period : restaurant.getOpeningPeriods()) {
            if(period.getOpeningDay() == Calendar.MONDAY && period.getClosingDay() == Calendar.TUESDAY) {
                assertEquals(8, period.getOpeningHour());
                assertEquals(0, period.getOpeningMinute());
                assertEquals(2, period.getClosingHour());
                assertEquals(0, period.getClosingMinute());
            }
        }
    }

    @Test
    public void test_OpeningData_givenNoHours() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithNoHours();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertFalse(restaurant.isOpeningHoursAvailable());
    }

    @Test
    public void test_OpeningData_givenAlwaysOpen() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsAlwaysOpen();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertTrue(restaurant.isAlwaysOpen());
    }

    @Test
    public void test_WebSiteAndPhone_givenWebSiteAndPhone() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithWebSiteAndPhone();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getDetailsWebsiteLlVisibility(), View.VISIBLE);
        assertEquals(restaurant.getDetailsCallLlVisibility(), View.VISIBLE);
    }

    @Test
    public void test_WebSiteAndPhone_givenNoWebSiteAndPhone() {
        PlaceDetailsResults placeDetailsResults = generatePlaceDetailsWithNoWebSiteAndPhone();
        Restaurant restaurant = new Restaurant(placeDetailsResults.getPlaceDetails().getPlaceId());
        PlaceDetailsResultToRestaurantMapper mapper = new PlaceDetailsResultToRestaurantMapper(restaurant);

        restaurant = mapper.apply(placeDetailsResults);
        assertEquals(restaurant.getDetailsWebsiteLlVisibility(), View.GONE);
        assertEquals(restaurant.getDetailsCallLlVisibility(), View.GONE);
    }

    // ---------------
    // Generate data
    // ---------------

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


        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }

    private PlaceDetailsResults generatePlaceDetailsWithAddressComponent(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setAddressComponents(new ArrayList<>());
        AddressComponent streetNumber = new AddressComponent();
        streetNumber.setTypes(Collections.singletonList("street_number"));
        streetNumber.setLongName("62Bis");
        AddressComponent route = new AddressComponent();
        route.setTypes(Collections.singletonList("route"));
        route.setLongName("Rue des Rosiers");
        placeDetails.getAddressComponents().add(streetNumber);
        placeDetails.getAddressComponents().add(route);

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithRouteAddressComponent(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setAddressComponents(new ArrayList<>());
        AddressComponent route = new AddressComponent();
        route.setTypes(Collections.singletonList("route"));
        route.setLongName("Rue des Rosiers");
        placeDetails.getAddressComponents().add(route);

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithVicinity(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setVicinity("62Bis, Rue des Rosiers");

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithPhotoRef(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        Photo photo = new Photo();
        photo.setPhotoReference("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp");

        placeDetails.setPhotos(new ArrayList<>());
        placeDetails.getPhotos().add(photo);

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithNoPhotoRef(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithRating(float rating){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setRating(rating);

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithOneOpeningPeriodByDay(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setUtcOffset(120);
        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
            Period period = new Period();

            period.setOpen(new Open());
            period.getOpen().setDay(i);
            period.getOpen().setTime("1130");

            period.setClose(new Close());
            period.getClose().setDay(i);
            period.getClose().setTime("1800");

            placeDetails.getOpeningHours().getPeriods().add(period);
        }

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithTwoOpeningPeriodByDayExceptSunday(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setUtcOffset(120);
        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = Calendar.MONDAY; i < Calendar.DAY_OF_WEEK; i++) {
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

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithOpeningPeriodCloseAtMidnight() {
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setUtcOffset(120);
        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = Calendar.MONDAY; i < Calendar.DAY_OF_WEEK; i++) {
            Period period = new Period();

            period.setOpen(new Open());
            period.getOpen().setDay(i);
            period.getOpen().setTime("0800");

            period.setClose(new Close());
            period.getClose().setDay(i+1);
            period.getClose().setTime("0000");

            placeDetails.getOpeningHours().getPeriods().add(period);
        }

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithOpeningPeriodCloseAfterMidnight() {
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setUtcOffset(120);
        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());

        for (int i = Calendar.MONDAY; i < Calendar.DAY_OF_WEEK; i++) {
            Period period = new Period();

            period.setOpen(new Open());
            period.getOpen().setDay(i);
            period.getOpen().setTime("0800");

            period.setClose(new Close());
            period.getClose().setDay(i+1);
            period.getClose().setTime("0200");

            placeDetails.getOpeningHours().getPeriods().add(period);
        }

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithNoHours(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsAlwaysOpen() {
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        placeDetails.setUtcOffset(120);
        placeDetails.setOpeningHours(new OpeningHours());
        placeDetails.getOpeningHours().setOpenNow(true);
        placeDetails.getOpeningHours().setPeriods(new ArrayList<>());
        Period period = new Period();
        period.setOpen(new Open());
        period.getOpen().setDay(0);
        period.getOpen().setTime("0000");
        placeDetails.getOpeningHours().getPeriods().add(period);


        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithWebSiteAndPhone(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");
        placeDetails.setInternationalPhoneNumber("+33 4 50 38 86 23");
        placeDetails.setWebsite("http://www.mizuki-sushi.fr/");

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }

    private PlaceDetailsResults generatePlaceDetailsWithNoWebSiteAndPhone(){
        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setName("Mizuki");
        placeDetails.setPlaceId("ChIJmZKgsilwjEcRKVdZd_cE-4k");

        PlaceDetailsResults placeDetailsResults = new PlaceDetailsResults();
        placeDetailsResults.setPlaceDetails(placeDetails);

        return placeDetailsResults;
    }
}
