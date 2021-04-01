package com.jeremydufeux.go4lunch.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ChosenRestaurantSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.SuccessLiveEvent;
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
public class WorkmatesRepository {

    private final PublishSubject<LiveEvent> mTaskResultObservable =PublishSubject.create();
    private final BehaviorSubject<Workmate> mCurrentUserObservable = BehaviorSubject.create();
    private final BehaviorSubject<List<Workmate>> mWorkmateListObservable = BehaviorSubject.create();

    private String mCurrentUserUId;

    @Inject
    public WorkmatesRepository() {
    }

    public void setCurrentUser(String uId){
        mCurrentUserUId = uId;
    }

    public void getOrCreateWorkmate(Workmate workmate) {
        mCurrentUserUId = workmate.getUId();
        WorkmateHelper.getWorkmateWithId(mCurrentUserUId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mTaskResultObservable.onNext(new CreateWorkmateSuccessLiveEvent());
                        } else {
                            createWorkmate(workmate);
                        }
                    }
                })
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }

    public void createWorkmate(Workmate workmate) {
        WorkmateHelper.createWorkmate(workmate)
                .addOnSuccessListener(aVoid -> mTaskResultObservable.onNext(new CreateWorkmateSuccessLiveEvent()))
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }

    public Observable<Workmate> observeCurrentUser() {
        WorkmateHelper.getWorkmateReferenceWithId(mCurrentUserUId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                        return;
                    }
                    if(value != null) {
                        mCurrentUserObservable.onNext(Objects.requireNonNull(value.toObject(Workmate.class)));
                    }
                });
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
        WorkmateHelper.setChosenRestaurantForCurrentUser(mCurrentUserUId, restaurantUId, restaurantName, chosenDate)
                .addOnSuccessListener(aVoid -> mTaskResultObservable.onNext(new ChosenRestaurantSuccessLiveEvent()))
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        WorkmateHelper.setLikedRestaurantForCurrentUser(mCurrentUserUId, likedRestaurants)
                .addOnSuccessListener(aVoid -> mTaskResultObservable.onNext(new ChosenRestaurantSuccessLiveEvent()))
                .addOnFailureListener(e -> mTaskResultObservable.onNext(new ErrorLiveEvent(e)));
    }
}


