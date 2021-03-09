package com.jeremydufeux.go4lunch.api;

import com.jeremydufeux.go4lunch.models.PlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.PlaceResult.PlaceResults;

import io.reactivex.Observable;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceResults> fetchNearbyPlaces(@Query("key") String apiKey, @Query("location") String latlng, @Query("radius") String radius, @Query("type") String type);

    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceResults> fetchNextPageNearbyPlaces(@Query("key") String apiKey, @Query("pagetoken") String pageToken);

    @GET("/maps/api/place/details/json")
    Observable<PlaceDetailsResults> fetchDetailsForPlaceId(@Query("key") String apiKey, @Query("place_id") String placeId, @Query("fields") String fields);

    //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //.client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
            .build();
}

