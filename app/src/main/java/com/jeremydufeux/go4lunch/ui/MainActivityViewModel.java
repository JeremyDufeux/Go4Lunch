package com.jeremydufeux.go4lunch.ui;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MainActivityViewModel";

    private final WorkmatesRepository mWorkmatesRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public MainActivityViewModel(WorkmatesRepository workmatesRepository) {
        mWorkmatesRepository = workmatesRepository;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCurrentUserLiveData::setValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeCurrentUser: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    public LiveData<LiveEvent> observeEvents(){
        return mSingleLiveEvent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables() {
        mDisposable.clear();
    }
}
