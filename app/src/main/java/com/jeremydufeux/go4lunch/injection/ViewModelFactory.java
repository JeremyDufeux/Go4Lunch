package com.jeremydufeux.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.ui.MainActivityViewModel;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.ListViewViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.LoginViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.MapViewViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.RestaurantDetailsViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WorkmatesRepository mWorkmatesRepository;
    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;
    private final UserDataRepository mUserDataRepository;
    private final Executor mExecutor;

    public ViewModelFactory(WorkmatesRepository workmatesRepository,
                            GooglePlacesRepository googlePlacesRepository,
                            RestaurantRepository restaurantRepository,
                            UserDataRepository userDataRepository,
                            Executor executor) {
        mWorkmatesRepository = workmatesRepository;
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
        mUserDataRepository = userDataRepository;
        mExecutor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(MainActivityViewModel.class)){
            return (T) new MainActivityViewModel(mWorkmatesRepository, mUserDataRepository);
        }
        else if(modelClass.isAssignableFrom(SharedViewModel.class)){
            return (T) new SharedViewModel();
        }
        else if(modelClass.isAssignableFrom(ListViewViewModel.class)){
            return (T) new ListViewViewModel(mRestaurantRepository, mUserDataRepository);
        }
        else if(modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(mWorkmatesRepository, mExecutor);
        }
        else if(modelClass.isAssignableFrom(MapViewViewModel.class)){
            return (T) new MapViewViewModel(mGooglePlacesRepository, mRestaurantRepository, mUserDataRepository, mExecutor);
        }
        else if(modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)){
            return (T) new RestaurantDetailsViewModel(mRestaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
