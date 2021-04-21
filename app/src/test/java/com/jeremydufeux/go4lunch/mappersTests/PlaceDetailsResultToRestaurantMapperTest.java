package com.jeremydufeux.go4lunch.mappersTests;

import com.jeremydufeux.go4lunch.mappers.PlaceDetailsResultToRestaurantMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Close;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsGeometry;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Open;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.OpeningHours;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Photo;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

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

        Photo photo = new Photo();
        photo.setPhotoReference("ATtYBwJo7zHiJORkGmRCpOD8ig6tRng0akzKblJtuxoQth0O2-DQXmp-EPynI1qGkpz8PEjDpKOufkty-kt9jEH4i-5_xD0v-GpVmbLoesD9OqFEb-Bj_NQa0MAJRZIoiYxP4C0j64MWMRcDECscR2KMHV0zl6TRqumQAi0AnjzwVVmCepIp");

        placeDetails.setPhotos(new ArrayList<>());
        placeDetails.getPhotos().add(photo);

        PlaceDetailsResults result = new PlaceDetailsResults();
        result.setPlaceDetails(placeDetails);

        return result;
    }
}
