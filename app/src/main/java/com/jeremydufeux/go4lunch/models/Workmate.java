package com.jeremydufeux.go4lunch.models;

import com.google.firebase.Timestamp;

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
    private Timestamp mLastChosenRestaurantDate;

    // For Ui
    private int mWorkmateChosenTvVisibility;
    private int mWorkmateNotChosenTvVisibility;

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
        this.mUId = uId;
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
        this.mEmail = email;
    }

    @Nullable
    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(@Nullable String pictureUrl) {
        this.mPictureUrl = pictureUrl;
    }

    public String getChosenRestaurantId() {
        return mChosenRestaurantId;
    }

    public void setChosenRestaurantId(String chosenRestaurantId) {
        this.mChosenRestaurantId = chosenRestaurantId;
    }

    public Timestamp getLastChosenRestaurantDate() {
        return mLastChosenRestaurantDate;
    }

    public void setLastChosenRestaurantDate(Timestamp lastChosenRestaurantDate) {
        this.mLastChosenRestaurantDate = lastChosenRestaurantDate;
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
}
