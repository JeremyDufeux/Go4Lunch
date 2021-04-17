package com.jeremydufeux.go4lunch.service;

import com.jeremydufeux.go4lunch.api.PlacesService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FakePlaceServiceProvider {
    public static PlacesService provideService(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new FakeInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(PlacesService.class);
    }
}
