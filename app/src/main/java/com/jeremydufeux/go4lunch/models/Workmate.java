package com.jeremydufeux.go4lunch.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class Workmate {
    private String mUId;
    private String mFullName;
    private String mNickname;
    @Nullable
    private String mEmail;
    @Nullable
    private String mPictureUrl;

    private String mChosenRestaurantId = "";
    private String mChosenRestaurantName = "";
    private Date mChosenRestaurantDate;

    private List<String> mLikedRestaurants = new ArrayList<>();

    // For Ui
    @Exclude private int mWorkmateChosenTvVisibility;
    @Exclude private int mWorkmateNotChosenTvVisibility;
    @Exclude private int mWorkmateGoFabColor;
    @Exclude private int mWorkmateLikedRestaurantTvText;
    @Exclude private int mWorkmateLikedRestaurantTvColor;

    public Workmate() {
    }

    public Workmate(String uId, String fullName, String nickname, @Nullable String email, @Nullable String pictureUrl) {
        mUId = uId;
        mFullName = fullName;
        mNickname = nickname;
        mEmail = email;
        mPictureUrl = pictureUrl;
    }

    public String getUId() {
        return mUId;
    }

    public void setUId(String uId) {
        mUId = uId;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
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

    public Date getChosenRestaurantDate() {
        return mChosenRestaurantDate;
    }

    public void setChosenRestaurantDate(Date chosenRestaurantDate) {
        mChosenRestaurantDate = chosenRestaurantDate;
    }

    public String getChosenRestaurantName() {
        return mChosenRestaurantName;
    }

    public void setChosenRestaurantName(String chosenRestaurantName) {
        mChosenRestaurantName = chosenRestaurantName;
    }

    public List<String> getLikedRestaurants() {
        return mLikedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        mLikedRestaurants = likedRestaurants;
    }

    @Exclude
    public int getWorkmateChosenTvVisibility() {
        return mWorkmateChosenTvVisibility;
    }

    @Exclude
    public void setWorkmateChosenTvVisibility(int workmateChosenTvVisibility) {
        mWorkmateChosenTvVisibility = workmateChosenTvVisibility;
    }

    @Exclude
    public int getWorkmateNotChosenTvVisibility() {
        return mWorkmateNotChosenTvVisibility;
    }

    @Exclude
    public void setWorkmateNotChosenTvVisibility(int workmateNotChosenTvVisibility) {
        mWorkmateNotChosenTvVisibility = workmateNotChosenTvVisibility;
    }

    @Exclude
    public int getWorkmateGoFabColor() {
        return mWorkmateGoFabColor;
    }

    @Exclude
    public void setWorkmateGoFabColor(int workmateGoFabColor) {
        mWorkmateGoFabColor = workmateGoFabColor;
    }

    @Exclude
    public int getWorkmateLikedRestaurantTvText() {
        return mWorkmateLikedRestaurantTvText;
    }

    @Exclude
    public void setWorkmateLikedRestaurantTvText(int workmateLikedRestaurantTvText) {
        mWorkmateLikedRestaurantTvText = workmateLikedRestaurantTvText;
    }

    @Exclude
    public int getWorkmateLikedRestaurantTvColor() {
        return mWorkmateLikedRestaurantTvColor;
    }

    @Exclude
    public void setWorkmateLikedRestaurantTvColor(int workmateLikedRestaurantTvColor) {
        mWorkmateLikedRestaurantTvColor = workmateLikedRestaurantTvColor;
    }
}
