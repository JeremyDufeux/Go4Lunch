package com.jeremydufeux.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;
import com.jeremydufeux.go4lunch.ui.MainViewModel;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.LoginViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.MapViewViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WorkmatesDataRepository mWorkmatesDataRepository;
    private final PlacesDataRepository mPlacesDataRepository;
    private final Executor mExecutor;

    public ViewModelFactory(WorkmatesDataRepository workmatesDataRepository,
                            PlacesDataRepository placesDataRepository,
                            Executor executor) {
        mWorkmatesDataRepository = workmatesDataRepository;
        mPlacesDataRepository = placesDataRepository;
        mExecutor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(SharedViewModel.class)){
            return (T) new SharedViewModel(mPlacesDataRepository);
        }
        else if(modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(mWorkmatesDataRepository, mExecutor);
        }
        else if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(mWorkmatesDataRepository);
        }
        else if(modelClass.isAssignableFrom(MapViewViewModel.class)){
            return (T) new MapViewViewModel(mPlacesDataRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
