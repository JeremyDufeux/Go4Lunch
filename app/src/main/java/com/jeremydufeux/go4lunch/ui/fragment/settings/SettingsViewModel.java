package com.jeremydufeux.go4lunch.ui.fragment.settings;

import android.text.Editable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private static final String TAG = "SettingsViewModel";

    private final WorkmatesRepository mWorkmatesRepository;
    private final UserDataRepository mUserDataRepository;


    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public SettingsViewModel(WorkmatesRepository workmatesRepository, UserDataRepository userDataRepository) {
        mWorkmatesRepository = workmatesRepository;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers() {
        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCurrentUserLiveData::setValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeCurrentUser: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables() {
        mDisposable.clear();
    }

    public boolean isNotificationEnabled() {
        return mUserDataRepository.isNotificationEnabled();
    }

    public void deleteAccount() {
    }

    public void saveSettings(String nickname, boolean notificationEnabled) {
        mUserDataRepository.setNotificationEnabled(notificationEnabled);
        mWorkmatesRepository.updateCurrentUserNickname(nickname);
    }
}
