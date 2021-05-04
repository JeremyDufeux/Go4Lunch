package com.jeremydufeux.go4lunch.ui;

import android.database.MatrixCursor;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.AutocompleteToMatrixCursorMapper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MainActivityViewModel";

    private final GooglePlacesRepository mGooglePlacesRepository;
    private final WorkmatesRepository mWorkmatesRepository;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private Disposable mAutocompleteDisposable;

    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();
    private final MutableLiveData<MatrixCursor> mAutocompleteLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public MainActivityViewModel(GooglePlacesRepository googlePlacesRepository,
                                 WorkmatesRepository workmatesRepository,
                                 UserDataRepository userDataRepository) {
        mGooglePlacesRepository = googlePlacesRepository;
        mWorkmatesRepository = workmatesRepository;
        mUserDataRepository = userDataRepository;
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

        mDisposable.add(mWorkmatesRepository.observeTasksResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSingleLiveEvent::setValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeTasksResults: ", throwable);
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
        if(mAutocompleteDisposable != null) {
            mAutocompleteDisposable.dispose();
        }
    }

    // ---------------
    // Search queries
    // ---------------

    public void onQueryTextChange(String newText) {
        getPlacesAutocomplete(newText,
                mUserDataRepository.getMapViewCameraLatitude(),
                mUserDataRepository.getMapViewCameraLongitude(),
                mUserDataRepository.getMapViewCameraRadius());
    }

    public void getPlacesAutocomplete(String input, double latitude, double longitude, double radius) {
        mAutocompleteDisposable = mGooglePlacesRepository.getAutocompletePlaces(input, latitude, longitude, radius)
                .map(new AutocompleteToMatrixCursorMapper())
                .subscribeOn(Schedulers.io())
                .subscribe(mAutocompleteLiveData::postValue);
    }

    public LiveData<MatrixCursor> observeAutocomplete(){
        return mAutocompleteLiveData;
    }
}
