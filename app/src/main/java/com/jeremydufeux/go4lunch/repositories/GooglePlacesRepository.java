package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class GooglePlacesRepository {
    public static final int PLACE_SERVICE_TIMEOUT = 10;
    private String mNextPageToken;

    @Inject
    GooglePlacesRepository(){
    }

    public Observable<PlaceSearchResults> getNearbyPlaces(double latitude, double longitude, double radius){
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String latlng = latitude + "," + longitude;
        String type = "restaurant";

        return placesService.fetchNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, String.valueOf(radius), type)
                .subscribeOn(Schedulers.io())
                .map(results -> {
                    mNextPageToken = results.getNextPageToken();
                    return results;
                })
                .timeout(PLACE_SERVICE_TIMEOUT, TimeUnit.SECONDS);
    }

    public Observable<PlaceSearchResults> getNextPageNearbyPlaces() {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNextPageNearbyPlaces(BuildConfig.MAPS_API_KEY, mNextPageToken)
                .subscribeOn(Schedulers.io())
                .timeout(PLACE_SERVICE_TIMEOUT, TimeUnit.SECONDS);
    }

    public Observable<PlaceDetailsResults> getDetailsForPlaceId(String placeId) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String fields = "name,"
                + "address_component,"
                + "geometry,"
                + "vicinity,"
                + "opening_hours,"
                + "utc_offset,"
                + "photo,"
                + "international_phone_number,"
                + "website,"
                + "rating";

        return placesService.fetchDetailsForPlaceId(BuildConfig.MAPS_API_KEY, placeId, fields)
                .subscribeOn(Schedulers.io())
                .timeout(PLACE_SERVICE_TIMEOUT, TimeUnit.SECONDS);
    }
}
