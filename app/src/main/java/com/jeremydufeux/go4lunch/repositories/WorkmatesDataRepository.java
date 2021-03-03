package com.jeremydufeux.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jeremydufeux.go4lunch.api.FirestoreResult;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;

public class WorkmatesDataRepository{

    private final MutableLiveData<FirestoreResult> mResult;

    public WorkmatesDataRepository() {
        mResult = new MutableLiveData<>();
    }

    public void createWorkmate(Workmate workmate) {
        WorkmateHelper.createWorkmate(workmate).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mResult.setValue(new FirestoreResult(true));
            } else {
                mResult.setValue(new FirestoreResult(false, task.getException()));
            }
        });
    }

    public LiveData<FirestoreResult> observeResult() {
        return mResult;
    }
}

