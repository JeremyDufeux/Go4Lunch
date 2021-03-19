package com.jeremydufeux.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jeremydufeux.go4lunch.api.FirestoreResult;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;

public class WorkmatesRepository {

    private final MutableLiveData<FirestoreResult> mResult;

    public WorkmatesRepository() {
        mResult = new MutableLiveData<>();
    }

    public void createWorkmate(Workmate workmate) {
        WorkmateHelper.createWorkmate(workmate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mResult.setValue(new FirestoreResult(true));
            } else {
                mResult.setValue(new FirestoreResult(task.getException()));
            }
        });
    }

    public LiveData<Workmate> getWorkmateWithId(String uid) {
        MutableLiveData<Workmate> workmate = new MutableLiveData<>();
        WorkmateHelper.getWorkmateWithId(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                workmate.postValue(task.getResult().toObject(Workmate.class));
            }
        });

        return workmate;
    }

    public LiveData<FirestoreResult> observeResult() {
        return mResult;
    }
}

