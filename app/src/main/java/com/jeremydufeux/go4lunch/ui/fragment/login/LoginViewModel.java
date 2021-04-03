package com.jeremydufeux.go4lunch.ui.fragment.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.SignInSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.NavigateToMapFragmentLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class LoginViewModel extends ViewModel {

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
                .subscribeWith(getTasksResults()));
    }

    public DisposableObserver<LiveEvent> getTasksResults(){
        return new DisposableObserver<LiveEvent>() {
            @Override
            public void onNext(@NonNull LiveEvent event) {
                if(event instanceof SignInSuccessLiveEvent){
                    mSingleLiveEvent.setValue(new NavigateToMapFragmentLiveEvent());
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

    public void authWorkmate(FirebaseUser firebaseUser) {
        mExecutor.execute(() ->
            mWorkmatesRepository.authWorkmate(convertFirebaseUserToWorkmate(firebaseUser))
        );
    }

    private Workmate convertFirebaseUserToWorkmate(FirebaseUser firebaseUser){
        String uId = firebaseUser.getUid();
        String displayName = firebaseUser.getProviderData().get(1).getDisplayName();

        assert displayName != null;
        List<String> parts = Arrays.asList(displayName.split(" "));
        String firstName = parts.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if(i != parts.size()-1) sb.append(" ");
        }
        String lastName = sb.toString();

        String email = firebaseUser.getProviderData().get(1).getEmail();
        String pictureUrl = Objects.requireNonNull(firebaseUser.getProviderData().get(1).getPhotoUrl()).toString();

        return new Workmate(uId, displayName, firstName, lastName, email, pictureUrl);
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
