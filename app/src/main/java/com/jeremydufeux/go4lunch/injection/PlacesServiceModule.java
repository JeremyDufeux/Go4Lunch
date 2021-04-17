package com.jeremydufeux.go4lunch.injection;

import com.jeremydufeux.go4lunch.api.PlaceServiceProvider;
import com.jeremydufeux.go4lunch.api.PlacesService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PlacesServiceModule {

    @Provides
    PlacesService providePlacesService() {
        return PlaceServiceProvider.provideService();
    }
}
