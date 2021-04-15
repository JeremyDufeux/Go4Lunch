package com.jeremydufeux.go4lunch.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jeremydufeux.go4lunch.api.WorkmateHelper;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.ErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.SignOutLiveEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class WorkmatesRepository{
    private static final String TAG = "WorkmatesRepository";

    private final PublishSubject<LiveEvent> mTaskResultObservable = PublishSubject.create();
    private final BehaviorSubject<Workmate> mCurrentUserObservable = BehaviorSubject.create();
    private final BehaviorSubject<List<Workmate>> mWorkmateListObservable = BehaviorSubject.create();

    private Workmate mCurrentUser;

    @Inject
    public WorkmatesRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            startCurrentUserObserver(currentUser.getUid());
        }
    }

    public void authWorkmate() {
        mCurrentUser = convertFirebaseUserToWorkmate(getCurrentUser());
        WorkmateHelper.getWorkmateWithId(mCurrentUser.getUId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mCurrentUser = Objects.requireNonNull(document.toObject(Workmate.class));
                            startCurrentUserObserver(mCurrentUser.getUId());
                        } else {
                            WorkmateHelper.setWorkmate(mCurrentUser)
                                    .addOnSuccessListener(aVoid -> startCurrentUserObserver(mCurrentUser.getUId()))
                                    .addOnFailureListener(error -> {
                                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                                        Log.e(TAG, "authWorkmate: ", error);
                                    });
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                    Log.e(TAG, "authWorkmate: ", error);
                });
    }

    public void startCurrentUserObserver(String uId){
        WorkmateHelper.getWorkmateReferenceWithId(uId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                        Log.e(TAG, "startCurrentUserObserver: error.toString() : ", error);
                        return;
                    }
                    if(value != null && value.exists()) {
                        mCurrentUser = Objects.requireNonNull(value.toObject(Workmate.class));
                        mCurrentUserObservable.onNext(mCurrentUser);
                    } else {
                        mTaskResultObservable.onNext(new SignOutLiveEvent());
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
                        Log.e(TAG, "observeWorkmates: ", error);
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

    public Observable<List<Workmate>> getInterestedWorkmatesForRestaurants(String restaurantId){
        return Observable.create(
                emitter ->
                        WorkmateHelper.getTodayWorkmateInterestedForRestaurantId(restaurantId)
                                .addSnapshotListener((value, error) -> {
                                    if(error != null){
                                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                                        Log.e(TAG, "setChosenRestaurantForUserId: ", error);
                                    }
                                    if(value != null){
                                        List<Workmate> interestedWorkmates = new ArrayList<>();
                                        for (DocumentSnapshot doc : value.getDocuments()) {
                                            interestedWorkmates.add(doc.toObject(Workmate.class));
                                        }
                                        emitter.onNext(interestedWorkmates);
                                    }
                                }));
    }

    public void setChosenRestaurantForUserId(String restaurantUId, String restaurantName) {
        WorkmateHelper.setChosenRestaurantForUserId(mCurrentUser.getUId(), restaurantUId, restaurantName)
                .addOnFailureListener(error -> {
                    mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                    Log.e(TAG, "setChosenRestaurantForUserId: ", error);
                });
    }

    public void removeChosenRestaurantForUserId() {
        WorkmateHelper.removeChosenRestaurantForUserId(mCurrentUser.getUId())
                .addOnFailureListener(error -> {
                    mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                    Log.e(TAG, "removeChosenRestaurantForUserId: ", error);
                });
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        WorkmateHelper.setLikedRestaurantForCurrentUser(mCurrentUser.getUId(), likedRestaurants)
                .addOnFailureListener(error -> {
                    mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                    Log.e(TAG, "setLikedRestaurants: ", error);
                });
    }

    public Observable<LiveEvent> observeTasksResults(){
        return mTaskResultObservable;
    }

    public void updateCurrentUserNickname(String nickname) {
        if(!nickname.isEmpty() && !nickname.equals(mCurrentUser.getNickname())) {
            WorkmateHelper.updateWorkmateNickname(mCurrentUser.getUId(), nickname);
        }
    }

    public void updateCurrentUserProfileUrl(String url) {
        WorkmateHelper.updateCurrentUserProfileUrl(mCurrentUser.getUId(), url);
    }

    public void updateCurrentUserProfilePic(Uri uriNewProfilePic) {
        if(uriNewProfilePic != null) {
            String uuid = UUID.randomUUID().toString();

            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference(uuid);
            profilePicRef.putFile(uriNewProfilePic)
                    .addOnSuccessListener(taskSnapshot ->
                            profilePicRef.getDownloadUrl()
                                    .addOnSuccessListener(uri ->
                                            updateCurrentUserProfileUrl(uri.toString())))
                    .addOnFailureListener(error -> {
                        mTaskResultObservable.onNext(new ErrorLiveEvent(error));
                        Log.e(TAG, "setLikedRestaurants: ", error);
                    });
        }
    }

    public boolean isCurrentUserLoggedIn() {
        return getCurrentUser() != null;
    }

    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private Workmate convertFirebaseUserToWorkmate(FirebaseUser firebaseUser){
        String uId = firebaseUser.getUid();
        String fullName = firebaseUser.getProviderData().get(1).getDisplayName();

        assert fullName != null;
        List<String> parts = Arrays.asList(fullName.split(" "));
        String firstName = parts.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if(i != parts.size()-1) sb.append(" ");
        }

        String email = firebaseUser.getProviderData().get(1).getEmail();
        String pictureUrl = Objects.requireNonNull(firebaseUser.getProviderData().get(1).getPhotoUrl()).toString();

        return new Workmate(uId, fullName, firstName, email, pictureUrl);
    }
}


