package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.WorkmateToDetailsMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.util.Calendar;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class RestaurantDetailsViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;
    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    // TODO Cast snackbar event only to Main activity
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();
    private final MutableLiveData<Restaurant> mRestaurantLiveData = new MutableLiveData<>();
    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();

    @Inject
    public RestaurantDetailsViewModel(RestaurantRepository restaurantRepository,
                                      WorkmatesRepository workmatesRepository,
                                      Executor executor) {
        mRestaurantRepository = restaurantRepository;
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeTasksResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getTasksResults()));
    }

    public DisposableObserver<LiveEvent> getTasksResults(){
        return new DisposableObserver<LiveEvent>() {
            @Override
            public void onNext(@NonNull LiveEvent event) {
                if(event instanceof CreateWorkmateSuccessLiveEvent){
                    mSingleLiveEvent.setValue(event);
                } else if(event instanceof ErrorLiveEvent) {
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public void getRestaurantWithId(String placeId) {
        mDisposable.add(mRestaurantRepository.getRestaurantWithId(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRestaurantResult()));

        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.io())
                .map(new WorkmateToDetailsMapper(placeId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getCurrentUserResult()));
    }

    public DisposableObserver<Restaurant> getRestaurantResult(){
        return new DisposableObserver<Restaurant>() {
            @Override
            public void onNext(@NonNull Restaurant restaurant) {
                mRestaurantLiveData.setValue(restaurant);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public DisposableObserver<Workmate> getCurrentUserResult(){
        return new DisposableObserver<Workmate>() {
            @Override
            public void onNext(@NonNull Workmate workmate) {
                mCurrentUserLiveData.setValue(workmate);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public LiveData<Restaurant> observeRestaurant(){
        return mRestaurantLiveData;
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    public void choseRestaurant(Restaurant restaurant, Workmate workmate) {
        if(workmate.getChosenRestaurantId().equals(restaurant.getUId())) {
            mExecutor.execute(() -> mWorkmatesRepository.setChosenRestaurantForCurrentUser("","", 0L));
        } else {
            Calendar now = Calendar.getInstance();
            mExecutor.execute(() -> mWorkmatesRepository.setChosenRestaurantForCurrentUser(restaurant.getUId(), restaurant.getName(), now.getTimeInMillis()));
        }
    }

    public void likeRestaurant(Restaurant restaurant, Workmate workmate) {
        if(workmate.getLikedRestaurants().contains(restaurant.getUId())) {
            workmate.getLikedRestaurants().remove(restaurant.getUId());
            mExecutor.execute(() -> mWorkmatesRepository.setLikedRestaurants(workmate.getLikedRestaurants()));
        } else {
            workmate.getLikedRestaurants().add(restaurant.getUId());
            mExecutor.execute(() -> mWorkmatesRepository.setLikedRestaurants(workmate.getLikedRestaurants()));
        }
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
