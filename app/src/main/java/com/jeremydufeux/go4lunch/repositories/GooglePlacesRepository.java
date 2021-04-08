package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.PlaceAutocomplete;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class GooglePlacesRepository {
    public static final int PLACE_SERVICE_TIMEOUT = 10;
    public static final String PLACE_TYPE = "restaurant";
    public static final String AUTOCOMPLETE_PLACE_TYPE = "establishment";
    private String mNextPageToken;
    private String mSessionToken;

    @Inject
    GooglePlacesRepository(){
        generateSessionToken();
    }

    public Observable<PlaceSearchResults> getNearbyPlaces(double latitude, double longitude, double radius){
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String latlng = latitude + "," + longitude;

        return placesService.fetchNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, String.valueOf(radius), PLACE_TYPE)
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
                .map(results -> {
                    mNextPageToken = results.getNextPageToken();
                    return results;
                })
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

        return placesService.fetchDetailsForPlaceId(BuildConfig.MAPS_API_KEY, mSessionToken, placeId, fields)
                .subscribeOn(Schedulers.io())
                .map(results -> {
                    generateSessionToken();
                    return results;
                })
                .timeout(PLACE_SERVICE_TIMEOUT, TimeUnit.SECONDS);
    }

    public Observable<PlaceAutocomplete> getAutocompletePlaces(String input, double latitude, double longitude, double radius){
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String latlng = latitude + "," + longitude;

        return placesService.fetchPlaceAutocomplete(BuildConfig.MAPS_API_KEY, mSessionToken, input, latlng, String.valueOf(radius), AUTOCOMPLETE_PLACE_TYPE)
                .subscribeOn(Schedulers.io())
                .timeout(PLACE_SERVICE_TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean haveNextPageToken() {
        return mNextPageToken != null && !mNextPageToken.isEmpty();
    }

    private void generateSessionToken(){
        mSessionToken = UUID.randomUUID().toString();
    }
}
