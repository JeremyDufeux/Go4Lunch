package com.jeremydufeux.go4lunch.repositories;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.GooglePlacesService;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.Result;
import com.jeremydufeux.go4lunch.models.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class PlacesDataRepository {

    public Observable<List<Place>> getNearbyPlaces(String latlng, String radius, String type){
        GooglePlacesService googlePlacesService = GooglePlacesService.retrofit.create(GooglePlacesService.class);

        return googlePlacesService.fetchNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, radius, type)
                .map(placeResults -> {
                    List<Place> placeList = new ArrayList<>();

                    for(Result result : placeResults.getResults()){
                        Place place = new Place(result.getPlaceId(), result.getName());
                        place.setLatitude(result.getGeometry().getLocation().getLat());
                        place.setLongitude(result.getGeometry().getLocation().getLng());
                        if(result.getOpeningHours() != null) {
                            place.setOpeningHours(result.getOpeningHours());
                        }

                        placeList.add(place);
                    }

                    return placeList;
                })
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }
}
