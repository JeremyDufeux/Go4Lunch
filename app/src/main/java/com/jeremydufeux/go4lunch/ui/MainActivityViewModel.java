package com.jeremydufeux.go4lunch.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;
    private final UserDataRepository mUserDataRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();

    @Inject
    public MainActivityViewModel(WorkmatesRepository workmatesRepository, UserDataRepository userDataRepository) {
        mWorkmatesRepository = workmatesRepository;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getCurrentUserResult()));
    }

    public void setCurrentUser(String uId){
        mWorkmatesRepository.setCurrentUser(uId);
    }

    public DisposableObserver<Workmate> getCurrentUserResult(){
        return new DisposableObserver<Workmate>() {
            @Override
            public void onNext(@NonNull Workmate workmate) {
                mCurrentUserLiveData.setValue(workmate);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    public void setLocation(Location location){
        mUserDataRepository.setLocation(location);
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
