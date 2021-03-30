package com.jeremydufeux.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import javax.inject.Inject;

public class WorkmatesRepository {

    private final SingleLiveEvent<LiveEvent> mResult;

    @Inject
    public WorkmatesRepository() {
        mResult = new SingleLiveEvent<>();
    }

    public void createWorkmate(Workmate workmate) {
        WorkmateHelper.createWorkmate(workmate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mResult.setValue(new CreateWorkmateSuccessLiveEvent());
            } else {
                mResult.setValue(new CreateWorkmateErrorLiveEvent(task.getException()));
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

    public LiveData<LiveEvent> observeResult() {
        return mResult;
    }
}


