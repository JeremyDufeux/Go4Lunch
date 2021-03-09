package com.jeremydufeux.go4lunch.models;

import com.jeremydufeux.go4lunch.models.PlaceDetailsResult.OpeningHours;

public class Place {
    private final String mUId;
    private Double mLatitude;
    private Double mLongitude;
    private String mName;
    private String mAddress;
    private String mPhotoReference;
    private String mPhoneNumber;
    private String mWebsite;
    private float mRating;
    private int mWorkmatesInterested;
    private int mDistanceFromUser;
    private OpeningHours mOpeningHours;

    public Place(String placeId, String name) {
        mUId = placeId;
        mName = name;
    }

    public String getUId() {
        return mUId;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
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

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getWorkmatesInterested() {
        return mWorkmatesInterested;
    }

    public void setWorkmatesInterested(int workmatesInterested) {
        mWorkmatesInterested = workmatesInterested;
    }

    public int getDistanceFromUser() {
        return mDistanceFromUser;
    }

    public void setDistanceFromUser(int distanceFromUser) {
        mDistanceFromUser = distanceFromUser;
    }

    public OpeningHours getOpeningHours() {
        return mOpeningHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        mOpeningHours = openingHours;
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

    public String getPhotoReference() {
        return mPhotoReference;
    }

    public void setPhotoReference(String photoReference) {
        mPhotoReference = photoReference;
    }
}
