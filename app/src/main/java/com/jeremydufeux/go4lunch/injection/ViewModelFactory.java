package com.jeremydufeux.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jeremydufeux.go4lunch.repositories.GooglePlaceRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;
import com.jeremydufeux.go4lunch.ui.MainActivityViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.LoginFragmentViewModel;
import com.jeremydufeux.go4lunch.ui.fragment.MapViewFragmentViewModel;

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
        if(modelClass.isAssignableFrom(LoginFragmentViewModel.class)){
            return (T) new LoginFragmentViewModel(mWorkmatesDataRepository, mExecutor);
        }
        else if(modelClass.isAssignableFrom(MainActivityViewModel.class)){
            return (T) new MainActivityViewModel(mWorkmatesDataRepository);
        }
        else if(modelClass.isAssignableFrom(MapViewFragmentViewModel.class)){
            return (T) new MapViewFragmentViewModel(mGooglePlaceRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
