package com.jeremydufeux.go4lunch.ui.fragment.settings;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.RestaurantUseCase;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.CreateNotificationLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.RemoveLastNotificationWorkLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.jeremydufeux.go4lunch.utils.Utils.isToday;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private static final String TAG = "SettingsViewModel";

    private final RestaurantUseCase mRestaurantUseCase;
    private final WorkmatesRepository mWorkmatesRepository;
    private final UserDataRepository mUserDataRepository;

    private final MutableLiveData<Workmate> mCurrentUserLiveData = new MutableLiveData<>();
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final SingleLiveEvent<LiveEvent> mSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public SettingsViewModel(RestaurantUseCase restaurantUseCase,
                             WorkmatesRepository workmatesRepository,
                             UserDataRepository userDataRepository) {
        mRestaurantUseCase = restaurantUseCase;
        mWorkmatesRepository = workmatesRepository;
        mUserDataRepository = userDataRepository;
    }

    public void startObservers() {
        mDisposable.add(mWorkmatesRepository.observeTasksResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEventReceived,
                        throwable -> {
                            Log.e(TAG, "mRestaurantUseCase.observeErrors: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }
                ));

        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCurrentUserLiveData::setValue,
                        throwable -> {
                            Log.e(TAG, "mWorkmatesRepository.observeCurrentUser: ", throwable);
                            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
                        }));

    }

    private void onEventReceived(LiveEvent event) {
        if(event instanceof ErrorLiveEvent){
            mSingleLiveEvent.setValue(new ShowSnackbarLiveEvent(R.string.error));
        }
    }

    public LiveData<LiveEvent> observeEvents(){
        return mSingleLiveEvent;
    }

    public LiveData<Workmate> observeCurrentUser(){
        return mCurrentUserLiveData;
    }

    public boolean isNotificationEnabled() {
        return mUserDataRepository.isNotificationEnabled();
    }

    public void saveSettings(String nickname, boolean notificationEnabled, Uri uriNewProfilePic, int distanceUnit) {
        if(notificationEnabled != mUserDataRepository.isNotificationEnabled()) {
            if (notificationEnabled) {
                createNotification();
            } else {
                mSingleLiveEvent.postValue(new RemoveLastNotificationWorkLiveEvent());
            }
        }

        mUserDataRepository.setNotificationEnabled(notificationEnabled);
        mUserDataRepository.setDistanceUnit(distanceUnit);
        mWorkmatesRepository.updateCurrentUserNickname(nickname);
        mWorkmatesRepository.updateCurrentUserProfilePic(uriNewProfilePic);
    }

    private void createNotification(){
        mDisposable.add(mWorkmatesRepository.observeCurrentUser()
                .subscribeOn(Schedulers.computation())
                .flatMap((Function<Workmate, ObservableSource<Restaurant>>) workmate -> {
                    if(workmate.getChosenRestaurantDate() != null && isToday(workmate.getChosenRestaurantDate())){
                        return mRestaurantUseCase.getRestaurantWithId(workmate.getChosenRestaurantId());
                    }
                    return null;
                })
                .subscribe(restaurant -> {
                    if(restaurant != null){
                        mSingleLiveEvent.postValue(new CreateNotificationLiveEvent(restaurant));
                    }
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearDisposables();
    }

    public void clearDisposables() {
        mDisposable.clear();
    }

    public int getUserDistanceUnit() {
        return mUserDataRepository.getDistanceUnit();
    }
}
