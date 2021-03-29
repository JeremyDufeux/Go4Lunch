package com.jeremydufeux.go4lunch.ui.fragment.listView;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.mappers.UpdateRestaurantMapper;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewViewModel extends ViewModel {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Restaurant>> mRestaurantListLiveData;

    public ListViewViewModel(RestaurantRepository restaurantRepository, UserDataRepository userDataRepository) {
        mRestaurantListLiveData = new MutableLiveData<>();

        mDisposable.add(restaurantRepository.observeRestaurantDetailsList()
                .subscribeOn(Schedulers.computation())
                .map(new UpdateRestaurantMapper(userDataRepository.getLocation()))
                .subscribeWith(getRestaurantList()));
    }

    public DisposableObserver<List<Restaurant>> getRestaurantList(){
        return new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(@NonNull List<Restaurant> restaurantList) {
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
