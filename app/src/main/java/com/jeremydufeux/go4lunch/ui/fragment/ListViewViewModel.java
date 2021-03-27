package com.jeremydufeux.go4lunch.ui.fragment;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Restaurant>> mRestaurantListLiveData;

    public ListViewViewModel(RestaurantRepository restaurantRepository, UserDataRepository userDataRepository) {
        mRestaurantRepository = restaurantRepository;
        mUserDataRepository = userDataRepository;

        mRestaurantListLiveData = new MutableLiveData<>();

        mDisposable.add(mRestaurantRepository.observeRestaurantDetailsList()
                .subscribeOn(Schedulers.computation())
                .subscribeWith(getRestaurantList()));
    }

    public DisposableObserver<HashMap<String, Restaurant>> getRestaurantList(){
        return new DisposableObserver<HashMap<String, Restaurant>>() {
            @Override
            public void onNext(@NonNull HashMap<String, Restaurant> restaurantHashMap) {
                List<Restaurant> restaurantList = new ArrayList<>();
                Location location = mUserDataRepository.getLocation();

                for(Restaurant restaurant : restaurantHashMap.values()){
                    restaurant.calculateDistanceFromUser(location);
                    restaurant.determineOpening();
                    restaurant.determineWorkmatesViewVisibility();
                    restaurantList.add(restaurant);
                }
                mRestaurantListLiveData.postValue(restaurantList);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Debug", "onError getRestaurantList " + e.toString());
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public LiveData<List<Restaurant>> observeRestaurantList() {
        return mRestaurantListLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
