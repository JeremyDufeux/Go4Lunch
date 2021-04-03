package com.jeremydufeux.go4lunch.repositories;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class WorkmatesRepository{

    private final PublishSubject<LiveEvent> mTaskResultObservable = PublishSubject.create();
    private final BehaviorSubject<Workmate> mCurrentUserObservable = BehaviorSubject.create();
    private final BehaviorSubject<List<Workmate>> mWorkmateListObservable = BehaviorSubject.create();

    private Workmate mCurrentUser;

    @Inject
    public WorkmatesRepository() {
    }

    public void authWorkmate(Workmate workmate) {
        WorkmateHelper.getWorkmateWithId(workmate.getUId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mCurrentUser = Objects.requireNonNull(document.toObject(Workmate.class));
                            startCurrentUserObserver(mCurrentUser.getUId());
                        } else {
                            mCurrentUser = workmate;
                            WorkmateHelper.setWorkmate(workmate)
                                    .addOnSuccessListener(aVoid -> startCurrentUserObserver(mCurrentUser.getUId()))
                                    .addOnFailureListener(e ->{
                                        mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                                        Log.e("WorkmatesRepository", "authWorkmate: " + e.toString());
                                    });
                        }
                    }
                })
                .addOnFailureListener(e ->{
                    mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                    Log.e("WorkmatesRepository", "authWorkmate: " + e.toString());
                });
    }

    public void startCurrentUserObserver(String uId){
        WorkmateHelper.getWorkmateReferenceWithId(uId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                        Log.e("WorkmatesRepository", "startCurrentUserObserver: error.toString() : " + error.toString());
                        return;
                    }
                    if(value != null) {
                        mCurrentUser = Objects.requireNonNull(value.toObject(Workmate.class));
                        mCurrentUserObservable.onNext(mCurrentUser);
                    }
                });
    }

    public Observable<Workmate> observeCurrentUser() {
        return mCurrentUserObservable;
    }

    public Observable<List<Workmate>> observeWorkmates(){
        WorkmateHelper.getWorkmates()
                .addSnapshotListener((value, e) -> {
                    if(e != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                        Log.e("WorkmatesRepository", "observeWorkmates: " + e.toString());
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

    public Observable<LiveEvent> observeTasksResults(){
        return mTaskResultObservable;
    }

    public void setChosenRestaurantForUserId(String restaurantUId, String restaurantName) {
        WorkmateHelper.setChosenRestaurantForUserId(mCurrentUser.getUId(), restaurantUId, restaurantName)
                .addOnFailureListener(e ->{
                    mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                    Log.e("WorkmatesRepository", "setChosenRestaurantForUserId: " + e.toString());
                });
    }

    public void removeChosenRestaurantForUserId() {
        WorkmateHelper.removeChosenRestaurantForUserId(mCurrentUser.getUId())
                .addOnFailureListener(e ->{
                    mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                    Log.e("WorkmatesRepository", "removeChosenRestaurantForUserId: " + e.toString());
                });
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        WorkmateHelper.setLikedRestaurantForCurrentUser(mCurrentUser.getUId(), likedRestaurants)
                .addOnFailureListener(e ->{
                    mTaskResultObservable.onNext(new ErrorLiveEvent(e));
                    Log.e("WorkmatesRepository", "setLikedRestaurants: " + e.toString());
                });
    }
}


