package com.jeremydufeux.go4lunch.injection;

import com.jeremydufeux.go4lunch.repositories.GooglePlaceRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    private static WorkmatesDataRepository provideWorkmateDataRepository() {
        return new WorkmatesDataRepository();
    }

    private static GooglePlaceRepository provideGooglePlaceRepository() {
        return new GooglePlaceRepository();
    }


    public static ViewModelFactory provideViewModelFactory(){
        WorkmatesDataRepository workmatesDataRepository = provideWorkmateDataRepository();
        GooglePlaceRepository googlePlaceRepository = provideGooglePlaceRepository();
        Executor executor = provideExecutor();
        return new ViewModelFactory(workmatesDataRepository, googlePlaceRepository, executor);
    }

}
