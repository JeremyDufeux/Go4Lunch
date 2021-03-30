package com.jeremydufeux.go4lunch.ui.fragment.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    @Inject
    public LoginViewModel(WorkmatesRepository workmatesRepository, Executor executor) {
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void createWorkmate(Workmate workmate) {
        mExecutor.execute(() ->
                mWorkmatesRepository.createWorkmate(workmate)
        );
    }

    public LiveData<LiveEvent> observeResult(){
        return mWorkmatesRepository.observeResult();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
