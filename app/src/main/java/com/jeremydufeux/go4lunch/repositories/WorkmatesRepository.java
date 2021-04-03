package com.jeremydufeux.go4lunch.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ChosenRestaurantSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.SignInSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.SuccessLiveEvent;

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

    private final PublishSubject<LiveEvent> mTaskResultObservable =PublishSubject.create();
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
                            WorkmateHelper.createWorkmate(workmate)
                                    .addOnSuccessListener(aVoid -> startCurrentUserObserver(mCurrentUser.getUId()))
                                    .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
                        }
                    }
                })
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }

    public void startCurrentUserObserver(String uId){
        WorkmateHelper.getWorkmateReferenceWithId(uId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
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
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                    }
                    if(value != null) {
                        List<Workmate> workmateList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            workmateList.add(document.toObject(Workmate.class));
                        }
                        mWorkmateListObservable.onNext(workmateList);
                        mTaskResultObservable.onNext(new SuccessLiveEvent());
                    }
                });
        return mWorkmateListObservable;
    }

    public Observable<LiveEvent> observeTasksResults(){
        return mTaskResultObservable;
    }

    public void setChosenRestaurantForCurrentUser(String restaurantUId, String restaurantName, Long chosenDate) {
        WorkmateHelper.setChosenRestaurantForCurrentUser(mCurrentUser.getUId(), restaurantUId, restaurantName, chosenDate)
                .addOnSuccessListener(aVoid -> mTaskResultObservable.onNext(new ChosenRestaurantSuccessLiveEvent()))
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        WorkmateHelper.setLikedRestaurantForCurrentUser(mCurrentUser.getUId(), likedRestaurants)
                .addOnSuccessListener(aVoid -> mTaskResultObservable.onNext(new ChosenRestaurantSuccessLiveEvent()))
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }
}


