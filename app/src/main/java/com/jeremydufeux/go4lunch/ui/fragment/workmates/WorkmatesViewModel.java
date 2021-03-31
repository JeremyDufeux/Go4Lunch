package com.jeremydufeux.go4lunch.ui.fragment.workmates;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.mappers.WorkmateMapper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Workmate>> mWorkmateListLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public WorkmatesViewModel(WorkmatesRepository workmatesRepository){
        mWorkmatesRepository = workmatesRepository;
    }

    public void startObservers(){
        mDisposable.add(mWorkmatesRepository.observeErrors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getErrors()));

        mDisposable.add(mWorkmatesRepository.getWorkmates()
                .subscribeOn(Schedulers.computation())
                .map(new WorkmateMapper())
                .subscribeWith(getWorkmateList()));
    }

    private DisposableObserver<List<Workmate>> getWorkmateList() {
        return new DisposableObserver<List<Workmate>>() {
            @Override
            public void onNext(@NonNull List<Workmate> workmates) {
                mWorkmateListLiveData.postValue(workmates);
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

    public DisposableObserver<Exception> getErrors(){
        return new DisposableObserver<Exception>() {
            @Override
            public void onNext(@NonNull Exception exception) {
                if(exception instanceof TimeoutException){
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_timeout));
                }
                else if(exception instanceof UnknownHostException) {
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error_no_internet));
                }
                else {
                    mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public LiveData<List<Workmate>> observeWorkmateList(){
        return mWorkmateListLiveData;
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
