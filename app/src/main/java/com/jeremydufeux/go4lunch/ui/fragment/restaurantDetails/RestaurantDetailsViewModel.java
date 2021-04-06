package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.WorkmateToDetailsMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.useCases.RestaurantUseCase;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class RestaurantDetailsViewModel extends ViewModel {
    private static final String TAG = "RestaurantDetailsViewMo";

    private final RestaurantUseCase mRestaurantUseCase;
    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();
    private final MutableLiveData<Restaurant> mRestaurantLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Workmate>> mWorkmatesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();

    private String mLastRestaurantId = "";

    @Inject
    public RestaurantDetailsViewModel(RestaurantUseCase restaurantUseCase,
                                      WorkmatesRepository workmatesRepository,
                                      Executor executor) {
        mRestaurantUseCase = restaurantUseCase;
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeTasksResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        liveEvent ->
                                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error)),
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeTasksResults: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));
    }

    public void initViewModel(String restaurantId) {
        getRestaurantWithId(restaurantId);
        if(!mLastRestaurantId.equals(restaurantId)){
            clearWorkmatesLiveData();
        }
        mLastRestaurantId = restaurantId;
    }

    public void getRestaurantWithId(String placeId) {
        mDisposable.add(mRestaurantUseCase.getRestaurantWithId(placeId)
                .subscribeOn(Schedulers.computation())
                .subscribe(mRestaurantLiveData::postValue,
                        throwable -> {
                            Log.e(TAG, "mRestaurantRepository.getRestaurantWithId: ", throwable);
                            mSingleLiveEvent.postValue(new ShowSnackbarLiveEvent(R.string.error));
                }));

        mDisposable.add(mWorkmatesRepository.getInterestedWorkmatesForRestaurants(placeId)
                .subscribeOn(Schedulers.computation())
                .subscribe(mWorkmatesLiveData::postValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.getInterestedWorkmatesForRestaurants: ", throwable);
                            mSingleLiveEvent.postValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));

        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.computation())
                .map(new WorkmateToDetailsMapper(placeId))
                .subscribe(mCurrentUserLiveData::postValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeCurrentUser: ", throwable);
                            mSingleLiveEvent.postValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));
    }

    public LiveData<LiveEvent> observeEvents(){
        return mSingleLiveEvent;
    }

    public LiveData<Restaurant> observeRestaurant(){
        return mRestaurantLiveData;
    }

    public LiveData<List<Workmate>> observeWorkmates(){
        return mWorkmatesLiveData;
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    public void choseRestaurant(Restaurant restaurant, Workmate workmate) {
        mExecutor.execute(() -> {
            if(workmate.getChosenRestaurantId() != null && workmate.getChosenRestaurantId().equals(restaurant.getUId())) {
                mWorkmatesRepository.removeChosenRestaurantForUserId();
            } else {
                mWorkmatesRepository.setChosenRestaurantForUserId(restaurant.getUId(), restaurant.getName());
            }
        });
    }

    public void likeRestaurant(Restaurant restaurant, Workmate workmate) {
        if(workmate.getLikedRestaurants().contains(restaurant.getUId())) {
            workmate.getLikedRestaurants().remove(restaurant.getUId());
        } else {
            workmate.getLikedRestaurants().add(restaurant.getUId());
        }
        mExecutor.execute(() -> mWorkmatesRepository.setLikedRestaurants(workmate.getLikedRestaurants()));
    }

    public void clearWorkmatesLiveData(){
        mWorkmatesLiveData.setValue(new ArrayList<>());
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
