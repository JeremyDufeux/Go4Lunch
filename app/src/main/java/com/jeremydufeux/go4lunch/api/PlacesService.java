package com.jeremydufeux.go4lunch.api;

import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.PlaceAutocomplete;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.facebook.FacebookSdk.getCacheDir;

public interface PlacesService {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceSearchResults> fetchNearbyPlaces(@Query("key") String apiKey,
                                                     @Query("location") String latlng,
                                                     @Query("radius") String radius,
                                                     @Query("type") String type);

    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlaceSearchResults> fetchNextPageNearbyPlaces(@Query("key") String apiKey,
                                                             @Query("pagetoken") String pageToken);

    @GET("/maps/api/place/details/json")
    Observable<PlaceDetailsResults> fetchDetailsForPlaceId(@Query("key") String apiKey,
                                                           @Query("sessiontoken") String sessionToken,
                                                           @Query("place_id") String placeId,
                                                           @Query("fields") String fields);

    @GET("/maps/api/place/autocomplete/json")
    Observable<PlaceAutocomplete> fetchPlaceAutocomplete(@Query("key") String apiKey,
                                                         @Query("sessiontoken") String sessionToken,
                                                         @Query("input") String input,
                                                         @Query("location") String latlng,
                                                         @Query("radius") String radius,
                                                         @Query("type") String type);

    int cacheSize = 10 * 1024 * 1024; // 10 MB
    Cache cache = new Cache(getCacheDir(), cacheSize);

    //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cache(cache)
            //.addInterceptor(interceptor)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build();
}

