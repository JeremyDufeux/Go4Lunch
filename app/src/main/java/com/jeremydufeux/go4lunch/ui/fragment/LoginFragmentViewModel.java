package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.api.FirestoreResult;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;

import java.util.concurrent.Executor;

public class LoginFragmentViewModel extends ViewModel {

    private final WorkmatesDataRepository mWorkmatesDataRepository;
    private final Executor executor;

    public LoginFragmentViewModel(WorkmatesDataRepository workmatesDataRepository, Executor executor) {
        this.mWorkmatesDataRepository = workmatesDataRepository;
        this.executor = executor;
    }

    public void createWorkmate(Workmate workmate) {
        executor.execute(() ->
                mWorkmatesDataRepository.createWorkmate(workmate)
        );
    }

    public LiveData<FirestoreResult> observeResult(){
        return mWorkmatesDataRepository.observeResult();
    }
}
