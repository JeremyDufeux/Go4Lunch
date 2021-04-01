package com.jeremydufeux.go4lunch.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Workmate {
    private String mUId;
    private String mDisplayName;
    private String mFirstName;
    private String mLastName;
    @Nullable
    private String mEmail;
    @Nullable
    private String mPictureUrl;

    private String mChosenRestaurantId;
    private String mChosenRestaurantName;
    private long mLastChosenRestaurantDate;

    private List<String> mLikedRestaurants = new ArrayList<>();

    // For Ui
    @Exclude private int mWorkmateChosenTvVisibility;
    @Exclude private int mWorkmateNotChosenTvVisibility;
    @Exclude private int mWorkmateGoFabColor;
    @Exclude private int mWorkmateLikedRestaurantTvText;
    @Exclude private int mWorkmateLikedRestaurantTvColor;

    public Workmate() {
    }

    public Workmate(String uId, String displayName, String firstName, @Nullable String lastName, @Nullable String email, @Nullable String pictureUrl) {
        mUId = uId;
        mDisplayName = displayName;
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mPictureUrl = pictureUrl;
    }

    public String getUId() {
        return mUId;
    }

    public void setUId(String uId) {
        mUId = uId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(@Nullable String lastName) {
        mLastName = lastName;
    }

    @Nullable
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(@Nullable String email) {
        mEmail = email;
    }

    @Nullable
    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(@Nullable String pictureUrl) {
        mPictureUrl = pictureUrl;
    }

    public String getChosenRestaurantId() {
        return mChosenRestaurantId;
    }

    public void setChosenRestaurantId(String chosenRestaurantId) {
        mChosenRestaurantId = chosenRestaurantId;
    }

    public long getLastChosenRestaurantDate() {
        return mLastChosenRestaurantDate;
    }

    public void setLastChosenRestaurantDate(long lastChosenRestaurantDate) {
        mLastChosenRestaurantDate = lastChosenRestaurantDate;
    }

    public String getChosenRestaurantName() {
        return mChosenRestaurantName;
    }

    public void setChosenRestaurantName(String chosenRestaurantName) {
        mChosenRestaurantName = chosenRestaurantName;
    }

    public int getWorkmateChosenTvVisibility() {
        return mWorkmateChosenTvVisibility;
    }

    public void setWorkmateChosenTvVisibility(int workmateChosenTvVisibility) {
        mWorkmateChosenTvVisibility = workmateChosenTvVisibility;
    }

    public int getWorkmateNotChosenTvVisibility() {
        return mWorkmateNotChosenTvVisibility;
    }

    public void setWorkmateNotChosenTvVisibility(int workmateNotChosenTvVisibility) {
        mWorkmateNotChosenTvVisibility = workmateNotChosenTvVisibility;
    }

    public int getWorkmateGoFabColor() {
        return mWorkmateGoFabColor;
    }

    public void setWorkmateGoFabColor(int workmateGoFabColor) {
        mWorkmateGoFabColor = workmateGoFabColor;
    }

    public List<String> getLikedRestaurants() {
        return mLikedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        mLikedRestaurants = likedRestaurants;
    }

    public int getWorkmateLikedRestaurantTvText() {
        return mWorkmateLikedRestaurantTvText;
    }

    public void setWorkmateLikedRestaurantTvText(int workmateLikedRestaurantTvText) {
        mWorkmateLikedRestaurantTvText = workmateLikedRestaurantTvText;
    }

    public int getWorkmateLikedRestaurantTvColor() {
        return mWorkmateLikedRestaurantTvColor;
    }

    public void setWorkmateLikedRestaurantTvColor(int workmateLikedRestaurantTvColor) {
        mWorkmateLikedRestaurantTvColor = workmateLikedRestaurantTvColor;
    }
}
