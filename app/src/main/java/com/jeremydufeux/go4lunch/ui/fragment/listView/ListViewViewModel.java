package com.jeremydufeux.go4lunch.ui.fragment.listView;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.RestaurantToListViewMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class ListViewViewModel extends ViewModel {
    private static final String TAG = "ListViewViewModel";

    private final RestaurantRepository mRestaurantRepository;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Restaurant>> mRestaurantListLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public ListViewViewModel(RestaurantRepository restaurantRepository, UserDataRepository userDataRepository) {
        mRestaurantRepository = restaurantRepository;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers(){
        mDisposable.add(mRestaurantRepository.observeRestaurantList()
                .subscribeOn(Schedulers.computation())
                .map(new RestaurantToListViewMapper(mUserDataRepository.getLocation()))
                .subscribe(mRestaurantListLiveData::postValue,
                        throwable -> {
                    Log.e(TAG, "mRestaurantRepository.observeRestaurantList: ", throwable);
                    mSingleLiveEvent.postValue(new ShowSnackbarLiveEvent(R.string.error));
                }));
    }

    public LiveData<List<Restaurant>> observeRestaurantList() {
        return mRestaurantListLiveData;
    }

    public LiveData<LiveEvent> observeEvents(){
        return mSingleLiveEvent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables(){
        mDisposable.clear();
    }
}
