package com.jeremydufeux.go4lunch.injection;

import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.useCases.RestaurantUseCase;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    private static WorkmatesRepository provideWorkmateDataRepository() {
        return new WorkmatesRepository();
    }

    private static RestaurantRepository provideRestaurantRepository() {
        return RestaurantRepository.getInstance();
    }

    private static GooglePlacesRepository provideGooglePlaceRepository() {
        return GooglePlacesRepository.getInstance();
    }

    private static UserDataRepository provideUserLocationRepository() {
        return UserDataRepository.getInstance();
    }

    private static RestaurantUseCase provideRestaurantUseCase(GooglePlacesRepository googlePlacesRepository, RestaurantRepository restaurantRepository) {
        return new RestaurantUseCase(googlePlacesRepository, restaurantRepository);
    }

    public static ViewModelFactory provideViewModelFactory(){
        WorkmatesRepository workmatesRepository = provideWorkmateDataRepository();
        GooglePlacesRepository googlePlacesRepository = provideGooglePlaceRepository();
        RestaurantRepository restaurantRepository = provideRestaurantRepository();
        UserDataRepository userDataRepository = provideUserLocationRepository();
        RestaurantUseCase restaurantUseCase = provideRestaurantUseCase(googlePlacesRepository, restaurantRepository);
        Executor executor = provideExecutor();
        return new ViewModelFactory(workmatesRepository, restaurantRepository, userDataRepository, restaurantUseCase, executor);
    }

}
