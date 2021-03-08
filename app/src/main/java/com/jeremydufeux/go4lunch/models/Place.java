package com.jeremydufeux.go4lunch.models;

import com.jeremydufeux.go4lunch.models.GooglePlaceResult.OpeningHours;

public class Place {
    private String mUId;
    private Double mLatitude;
    private Double mLongitude;
    private String mName;
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

    public OpeningHours getOpeningHours() {
        return mOpeningHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        mOpeningHours = openingHours;
    }
}
