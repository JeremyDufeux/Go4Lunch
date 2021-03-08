package com.jeremydufeux.go4lunch.ui.fragment;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;
import com.jeremydufeux.go4lunch.repositories.PlacesDataRepository;

import java.util.concurrent.Executor;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MapViewViewModel extends ViewModel {

    private final PlacesDataRepository mPlacesDataRepository;
    private final Executor mExecutor;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<GooglePlaceResults> mGooglePlaceList;

    public MapViewViewModel(PlacesDataRepository placesDataRepository, Executor executor) {
        mPlacesDataRepository = placesDataRepository;
        mExecutor = executor;

        mGooglePlaceList = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null) {
            mDisposable.clear();
        }
    }
}
