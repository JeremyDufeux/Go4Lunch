package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.GooglePlacesService;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceRepository {

    public Observable<GooglePlaceResults> fetchNearbyPlaces(String latlng, String radius, String type){
        GooglePlacesService googlePlacesService = GooglePlacesService.retrofit.create(GooglePlacesService.class);

        return googlePlacesService.getNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, radius, type)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }
}
