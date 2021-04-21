package com.jeremydufeux.go4lunch.mappersTests;

import com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultToRestaurantMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.Location;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchGeometry;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jeremydufeux.go4lunch.mappers.NearbyPlacesResultToRestaurantMapper.OPERATIONAL_BUSINESS_STATUS;
import static org.junit.Assert.assertEquals;

public class NearbyPlacesResultToRestaurantMapperTest {

    @Test
    public void test_valuesMappedCorrectly(){
        PlaceSearchResults placeSearchResults = generateNearbySearchFirstResults();
        NearbyPlacesResultToRestaurantMapper mapper = new NearbyPlacesResultToRestaurantMapper();

        List<Restaurant> restaurantList = mapper.apply(placeSearchResults);
        assertEquals(restaurantList.size(), placeSearchResults.getPlaceSearches().size());
        assertEquals(restaurantList.get(0).getName(), placeSearchResults.getPlaceSearches().get(0).getName());
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
}
