package com.jeremydufeux.go4lunch.ui.fragment.loginView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.api.FirestoreResult;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;

import java.util.concurrent.Executor;

public class LoginViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    public LoginViewModel(WorkmatesRepository workmatesRepository, Executor executor) {
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void createWorkmate(Workmate workmate) {
        mExecutor.execute(() ->
                mWorkmatesRepository.createWorkmate(workmate)
        );
    }

    public LiveData<FirestoreResult> observeResult(){
        return mWorkmatesRepository.observeResult();
    }
}
