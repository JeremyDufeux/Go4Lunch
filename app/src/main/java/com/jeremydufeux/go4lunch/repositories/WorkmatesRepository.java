package com.jeremydufeux.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class WorkmatesRepository {

    private final SingleLiveEvent<LiveEvent> mCreateWorkmateResult = new SingleLiveEvent<>();
    private final PublishSubject<Exception> mErrorObservable =PublishSubject.create();
    private final BehaviorSubject<List<Workmate>> mWorkmateListObservable = BehaviorSubject.create();

    @Inject
    public WorkmatesRepository() {
    }

    public void createWorkmate(Workmate workmate) {
        WorkmateHelper.createWorkmate(workmate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mCreateWorkmateResult.setValue(new CreateWorkmateSuccessLiveEvent());
            } else {
                mCreateWorkmateResult.setValue(new ErrorLiveEvent(task.getException()));
            }
        });
    }

    public LiveData<Workmate> getWorkmateWithId(String uid) {
        MutableLiveData<Workmate> workmate = new MutableLiveData<>();
        WorkmateHelper.getWorkmateWithId(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                workmate.postValue(task.getResult().toObject(Workmate.class));
            } else {
                mErrorObservable.onNext(task.getException());
            }
        });

        return workmate;
    }

    public Observable<List<Workmate>> getWorkmates(){
        WorkmateHelper.getWorkmates().addSnapshotListener((value, error) -> {
            if(error != null){
                mErrorObservable.onNext(error);
            }

            if(value != null) {
                List<Workmate> workmateList = new ArrayList<>();

                for (DocumentSnapshot document : value.getDocuments()) {
                    workmateList.add(document.toObject(Workmate.class));
                }
                mWorkmateListObservable.onNext(workmateList);
            }
        });
        return mWorkmateListObservable;
    }

    public Observable<Exception> observeErrors(){
        return mErrorObservable;
    }

    public LiveData<LiveEvent> observeCreateWorkmateResult() {
        return mCreateWorkmateResult;
    }
}


