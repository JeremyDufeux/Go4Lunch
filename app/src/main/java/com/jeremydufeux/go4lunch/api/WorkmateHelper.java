package com.jeremydufeux.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WorkmateHelper {

    private static final String COLLECTION_NAME = "workmates";

    // --- Collection ---
    public static CollectionReference getWorkmatesCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- Get all workmates ---
    public static CollectionReference getWorkmates(){
        return WorkmateHelper.getWorkmatesCollection();
    }

    // --- Get ---
    public static DocumentReference getWorkmateReferenceWithId(String uid){
        return WorkmateHelper.getWorkmatesCollection().document(uid);
    }

    public static Task<DocumentSnapshot> getWorkmateWithId(String uid){
        return WorkmateHelper.getWorkmatesCollection().document(uid).get();
    }

    // --- Create ---
    public static Task<Void> createWorkmate(Workmate workmate) {
        return WorkmateHelper.getWorkmatesCollection().document(workmate.getUId()).set(workmate);
    }

    // --- Update ---
    public static Task<Void> updateFirstName(String firstName, String uid) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("firstName", firstName);
    }

    public static Task<Void> updateLastName(String lastName, String uid) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("lastName", lastName);
    }

    public static Task<Void> updateEmail(String uid, String email) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("email", email);
    }

    public static Task<Void> updateChosenRestaurantId(String uid, String chosenRestaurantId) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("chosenRestaurantId", chosenRestaurantId);
    }

    public static Task<Void> updateLastChosenRestaurantDate(String uid, Date lastChosenRestaurantDate) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).update("lastChosenRestaurantDate", lastChosenRestaurantDate);
    }

    // --- Delete ---
    public static Task<Void> deleteWorkmate(String uid) {
        return WorkmateHelper.getWorkmatesCollection().document(uid).delete();
    }

    public static Task<Void> setChosenRestaurantForCurrentUser(String workmateUId, String restaurantUId, String restaurantName, long chosenDate) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chosenRestaurantId", restaurantUId);
        hashMap.put("chosenRestaurantName", restaurantName);
        hashMap.put("lastChosenRestaurantDate", chosenDate);
        return WorkmateHelper.getWorkmatesCollection().document(workmateUId).update(hashMap);
    }

    public static Task<Void> setLikedRestaurantForCurrentUser(String workmateUId, List<String> likedRestaurants) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("likedRestaurants", likedRestaurants);
        return WorkmateHelper.getWorkmatesCollection().document(workmateUId).update(hashMap);
    }
}