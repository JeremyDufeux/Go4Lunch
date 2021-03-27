package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GooglePlacesRepository {

    private String mNextPageToken;

    private static final GooglePlacesRepository INSTANCE = new GooglePlacesRepository();

    public static GooglePlacesRepository getInstance(){
        return INSTANCE;
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
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlaceSearchResults> getNextPageNearbyPlaces() {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNextPageNearbyPlaces(BuildConfig.MAPS_API_KEY, mNextPageToken)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlaceDetailsResults> getDetailsForPlaceId(String placeId) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String fields = "place_id,"
                + "name,"
                + "address_component,"
                + "vicinity,"
                + "geometry,"
                + "opening_hours,"
                + "utc_offset,"
                + "photo,"
                + "international_phone_number,"
                + "website,"
                + "rating";

        return placesService.fetchDetailsForPlaceId(BuildConfig.MAPS_API_KEY, placeId, fields)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }
}
