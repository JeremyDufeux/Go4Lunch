package com.jeremydufeux.go4lunch.api;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesService {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<GooglePlaceResults> fetchNearbyPlaces(@Query("key") String apiKey, @Query("location") String latlng, @Query("radius") String radius, @Query("type") String type);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}

