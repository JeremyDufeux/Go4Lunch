package com.jeremydufeux.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.HashMap;
import java.util.List;

import static com.jeremydufeux.go4lunch.utils.Utils.getTodayEndTimestamp;
import static com.jeremydufeux.go4lunch.utils.Utils.getTodayStartTimestamp;

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

    public static Query getTodayWorkmateInterestedForRestaurantId(String restaurantId){
        return WorkmateHelper.getWorkmatesCollection()
                .whereEqualTo("chosenRestaurantId", restaurantId)
                .whereGreaterThan("chosenRestaurantDate", getTodayStartTimestamp())
                .whereLessThan("chosenRestaurantDate", getTodayEndTimestamp());
    }

    // --- Create ---
    public static Task<Void> setWorkmate(Workmate workmate) {
        return WorkmateHelper.getWorkmatesCollection().document(workmate.getUId()).set(workmate);
    }

    // --- Update ---

    public static Task<Void> setChosenRestaurantForUserId(String workmateUId, String restaurantUId, String restaurantName) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chosenRestaurantId", restaurantUId);
        hashMap.put("chosenRestaurantName", restaurantName);
        hashMap.put("chosenRestaurantDate", FieldValue.serverTimestamp());
        return WorkmateHelper.getWorkmatesCollection().document(workmateUId).update(hashMap);
    }

    public static Task<Void> removeChosenRestaurantForUserId(String uId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chosenRestaurantId", "");
        hashMap.put("chosenRestaurantName", "");
        return WorkmateHelper.getWorkmatesCollection().document(uId).update(hashMap);
    }

    public static Task<Void> setLikedRestaurantForCurrentUser(String workmateUId, List<String> likedRestaurants) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("likedRestaurants", likedRestaurants);
        return WorkmateHelper.getWorkmatesCollection().document(workmateUId).update(hashMap);
    }

    public static void updateWorkmateNickname(String workmateUId, String nickname) {
        WorkmateHelper.getWorkmatesCollection().document(workmateUId).update("nickname", nickname);
    }
}