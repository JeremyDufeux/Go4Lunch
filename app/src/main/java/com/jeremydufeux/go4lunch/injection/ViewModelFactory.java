package com.jeremydufeux.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jeremydufeux.go4lunch.repositories.GooglePlaceRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;
import com.jeremydufeux.go4lunch.ui.MainViewModel;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.LoginViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.MapViewViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WorkmatesDataRepository mWorkmatesDataRepository;
    private final GooglePlaceRepository mGooglePlaceRepository;
    private final Executor mExecutor;

    public ViewModelFactory(WorkmatesDataRepository workmatesDataRepository,
                            GooglePlaceRepository googlePlaceRepository,
                            Executor executor) {
        mWorkmatesDataRepository = workmatesDataRepository;
        mGooglePlaceRepository = googlePlaceRepository;
        mExecutor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(SharedViewModel.class)){
            return (T) new SharedViewModel();
        }
        else if(modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(mWorkmatesDataRepository, mExecutor);
        }
        else if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(mWorkmatesDataRepository);
        }
        else if(modelClass.isAssignableFrom(MapViewViewModel.class)){
            return (T) new MapViewViewModel(mGooglePlaceRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
