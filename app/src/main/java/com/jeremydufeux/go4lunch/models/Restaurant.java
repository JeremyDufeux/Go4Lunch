package com.jeremydufeux.go4lunch.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>{
    private final String mUId;
    private String mName;
    private String mAddress;
    private String mPhoneNumber;
    private String mWebsite;
    private String mPhotoUrl;
    private float mRating;
    private Location mLocation;
    private int mDistanceFromUser;
    private final List<String> mInterestedWorkmates = new ArrayList<>();
    private final List<OpenPeriod> mOpeningPeriods = new ArrayList<>();
    private boolean mOpeningHoursAvailable;
    private boolean mAlwaysOpen;

    // For Ui
    private int mDistanceTvVisibility;
    private int mStar1IvVisibility;
    private int mStar2IvVisibility;
    private int mStar3IvVisibility;
    private int mOpenTvString;
    private String mOpenTvCloseTimeString;
    private int mOpenTvVisibility;
    private int mOpenTvColor;
    private int mWorkmateTvVisibility;
    private int mWorkmateIvVisibility;
    private int mDetailsCallLlVisibility;
    private int mDetailsWebsiteLlVisibility;
    private int mMarkerOptionIconResource;
    private int mDistanceUnitString;

    public Restaurant(String placeId) {
        mUId = placeId;
    }

    public String getUId() {
        return mUId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public int getDistanceFromUser() {
        return mDistanceFromUser;
    }

    public void setDistanceFromUser(int distanceFromUser) {
        mDistanceFromUser = distanceFromUser;
    }

    public List<String> getInterestedWorkmates() {
        return mInterestedWorkmates;
    }

    public List<OpenPeriod> getOpeningPeriods() {
        return mOpeningPeriods;
    }

    public boolean isOpeningHoursAvailable() {
        return mOpeningHoursAvailable;
    }

    public void setOpeningHoursAvailable(boolean openingHoursAvailable) {
        mOpeningHoursAvailable = openingHoursAvailable;
    }

    public boolean isAlwaysOpen() {
        return mAlwaysOpen;
    }

    public void setAlwaysOpen(boolean alwaysOpen) {
        mAlwaysOpen = alwaysOpen;
    }

    public int getDistanceTvVisibility() {
        return mDistanceTvVisibility;
    }

    public void setDistanceTvVisibility(int distanceTvVisibility) {
        mDistanceTvVisibility = distanceTvVisibility;
    }

    public int getStar1IvVisibility() {
        return mStar1IvVisibility;
    }

    public void setStar1IvVisibility(int star1IvVisibility) {
        mStar1IvVisibility = star1IvVisibility;
    }

    public int getStar2IvVisibility() {
        return mStar2IvVisibility;
    }

    public void setStar2IvVisibility(int star2IvVisibility) {
        mStar2IvVisibility = star2IvVisibility;
    }

    public int getStar3IvVisibility() {
        return mStar3IvVisibility;
    }

    public void setStar3IvVisibility(int star3IvVisibility) {
        mStar3IvVisibility = star3IvVisibility;
    }

    public int getOpenTvString() {
        return mOpenTvString;
    }

    public void setOpenTvString(int openTvString) {
        mOpenTvString = openTvString;
    }

    public String getOpenTvCloseTimeString() {
        return mOpenTvCloseTimeString;
    }

    public void setOpenTvCloseTimeString(String openTvCloseTimeString) {
        mOpenTvCloseTimeString = openTvCloseTimeString;
    }

    public int getOpenTvVisibility() {
        return mOpenTvVisibility;
    }

    public void setOpenTvVisibility(int openTvVisibility) {
        mOpenTvVisibility = openTvVisibility;
    }

    public int getOpenTvColor() {
        return mOpenTvColor;
    }

    public void setOpenTvColor(int openTvColor) {
        mOpenTvColor = openTvColor;
    }

    public int getWorkmateTvVisibility() {
        return mWorkmateTvVisibility;
    }

    public void setWorkmateTvVisibility(int workmateTvVisibility) {
        mWorkmateTvVisibility = workmateTvVisibility;
    }

    public int getWorkmateIvVisibility() {
        return mWorkmateIvVisibility;
    }

    public void setWorkmateIvVisibility(int workmateIvVisibility) {
        mWorkmateIvVisibility = workmateIvVisibility;
    }

    public int getDetailsCallLlVisibility() {
        return mDetailsCallLlVisibility;
    }

    public void setDetailsCallLlVisibility(int detailsCallLlVisibility) {
        mDetailsCallLlVisibility = detailsCallLlVisibility;
    }

    public int getDetailsWebsiteLlVisibility() {
        return mDetailsWebsiteLlVisibility;
    }

    public void setDetailsWebsiteLlVisibility(int detailsWebsiteLlVisibility) {
        mDetailsWebsiteLlVisibility = detailsWebsiteLlVisibility;
    }

    public int getMarkerOptionIconResource() {
        return mMarkerOptionIconResource;
    }

    public void setMarkerOptionIconResource(int markerOptionIconResource) {
        mMarkerOptionIconResource = markerOptionIconResource;
    }

    public int getDistanceUnitString() {
        return mDistanceUnitString;
    }

    public void setDistanceUnitString(int distanceUnitString) {
        mDistanceUnitString = distanceUnitString;
    }

    public int compareTo(Restaurant restaurant) {
        return mDistanceFromUser - restaurant.getDistanceFromUser();
    }
}
