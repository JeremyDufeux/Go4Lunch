package com.jeremydufeux.go4lunch.ui.fragment.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.NavigateToMapFragmentLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.SignInSuccessLiveEvent;

import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public LoginViewModel(WorkmatesRepository workmatesRepository, Executor executor) {
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeTasksResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::getTasksResults,
                        this::getErrors));
    }

    public void getTasksResults(LiveEvent event){
        if(event instanceof SignInSuccessLiveEvent){
            mSingleLiveEvent.setValue(new NavigateToMapFragmentLiveEvent());
        } else if(event instanceof ErrorLiveEvent) {
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
        }
    }

    public void getErrors(Throwable throwable){
        if(throwable instanceof TimeoutException){
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_timeout));
        }
        else if(throwable instanceof UnknownHostException) {
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_no_internet));
        }
        else {
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
        }
    }

    public void attemptToCreateWorkmate(Workmate workmate) {
        mExecutor.execute(() -> mWorkmatesRepository.attemptToCreateWorkmate(workmate));
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

    public void startCurrentUserObserver(String uid) {
        mWorkmatesRepository.startCurrentUserObserver(uid);
    }
}
