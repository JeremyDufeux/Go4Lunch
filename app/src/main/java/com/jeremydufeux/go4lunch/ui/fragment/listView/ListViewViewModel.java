package com.jeremydufeux.go4lunch.ui.fragment.listView;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.RestaurantToListViewMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantUseCase;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.StopRefreshLiveEvent;
import com.jeremydufeux.go4lunch.utils.NoMorePageException;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class ListViewViewModel extends ViewModel {
    private static final String TAG = "ListViewViewModel";

    private final RestaurantUseCase mRestaurantUseCase;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Restaurant>> mRestaurantListLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public ListViewViewModel(RestaurantUseCase restaurantUseCase,
                             UserDataRepository userDataRepository) {
        mRestaurantUseCase = restaurantUseCase;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers(){
        mDisposable.add(mRestaurantUseCase.observeErrors()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::getErrorLiveEvents,
                        throwable -> {
                            Log.e(TAG, "mRestaurantUseCase.observeErrors: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }
                ));

        mDisposable.add(mRestaurantUseCase.observeRestaurantDetailsList()
                .subscribeOn(Schedulers.computation())
                .map(new RestaurantToListViewMapper(mUserDataRepository.getLocation(), mUserDataRepository.getDistanceUnit()))
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

    public void getErrorLiveEvents(Throwable throwable){
        if(throwable instanceof TimeoutException){
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_timeout));
        }
        else if(throwable instanceof UnknownHostException) {
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_no_internet));
        }
        else if(throwable instanceof NoMorePageException) {
            mSingleLiveEvent.setValue(new StopRefreshLiveEvent());
        }
        else {
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables(){
        mDisposable.clear();
    }

    public void loadNextPage() {
        mRestaurantUseCase.loadNextPage();
    }
}
