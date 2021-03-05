package com.jeremydufeux.go4lunch.ui.fragment;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;
import com.jeremydufeux.go4lunch.repositories.GooglePlaceRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.observers.DisposableObserver;

public class MapViewFragmentViewModel extends ViewModel {

    private final GooglePlaceRepository mGooglePlaceRepository;
    private final Executor mExecutor;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<GooglePlaceResults> mGooglePlaceList;

    public MapViewFragmentViewModel(GooglePlaceRepository googlePlaceRepository, Executor executor) {
        mGooglePlaceRepository = googlePlaceRepository;
        mExecutor = executor;

        mGooglePlaceList = new MutableLiveData<>();
    }

    public void fetchNearbyPlaces(String latlng, String radius, String type) {
        mDisposable.add(mGooglePlaceRepository.fetchNearbyPlaces(latlng, radius, type)
                .subscribeWith(new DisposableObserver<GooglePlaceResults>() {
                    @Override
                    public void onNext(@NonNull GooglePlaceResults googlePlacesResults) {
                        mGooglePlaceList.postValue(googlePlacesResults);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Debug", "onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    public MutableLiveData<GooglePlaceResults> getGooglePlaceList() {
        return mGooglePlaceList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null) {
            mDisposable.clear();
        }
    }
}
