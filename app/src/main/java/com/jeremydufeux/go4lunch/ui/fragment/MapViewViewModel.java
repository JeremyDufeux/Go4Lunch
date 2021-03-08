package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.PlaceResults;
import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;

import java.util.concurrent.Executor;

import io.reactivex.disposables.CompositeDisposable;

public class MapViewViewModel extends ViewModel {

    private final PlacesDataRepository mPlacesDataRepository;
    private final Executor mExecutor;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public MapViewViewModel(PlacesDataRepository placesDataRepository, Executor executor) {
        mPlacesDataRepository = placesDataRepository;
        mExecutor = executor;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null) {
            mDisposable.clear();
        }
    }
}
