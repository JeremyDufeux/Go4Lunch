package com.jeremydufeux.go4lunch.models;

import android.location.Location;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>{
    private final String mUId;
    private String mName;
    private String mAddress;
    private String mPhoneNumber;
    private String mWebsite;
    private String mPhotoUrl;
    private float mRating;
    private final List<String> mInterestedWorkmates = new ArrayList<>();
    private Location mLocation;
    private int mDistanceFromUser;

    // For Opening
    /** The HashMap represent the week, the key is for the day of the week: 0 for monday, 6 for sunday,
     The list in value contain period of time where the place is open during the day, represented by the nested class OpenPeriod  **/
    private HashMap<Integer, List<OpenPeriod>>  mOpeningHours;
    private boolean mOpeningHoursAvailable;
    private boolean mAlwaysOpen;
    private int mUtcOffset;

    // For Google Maps
    private Marker mMarker;

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

    public void setAlwaysOpen(boolean alwaysOpen) {
        mAlwaysOpen = alwaysOpen;
    }

    public void setOpeningHours(HashMap<Integer, List<OpenPeriod>> openingHours) {
        mOpeningHours = openingHours;
    }

    public void setRating(float rating) {
        mRating = rating;
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

    public float getRating() {
        return mRating;
    }

    public List<String> getInterestedWorkmates() {
        return mInterestedWorkmates;
    }

    public void setUtcOffset(int utcOffset) {
        this.mUtcOffset = utcOffset;
    }

    public boolean isAlwaysOpen() {
        return mAlwaysOpen;
    }

    public int getUtcOffset() {
        return mUtcOffset;
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

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public int getOpenTvString() {
        return mOpenTvString;
    }

    public int getDistanceTvVisibility() {
        return mDistanceTvVisibility;
    }

    public int getStar1IvVisibility() {
        return mStar1IvVisibility;
    }

    public int getStar2IvVisibility() {
        return mStar2IvVisibility;
    }

    public int getStar3IvVisibility() {
        return mStar3IvVisibility;
    }

    public int getOpenTvVisibility() {
        return mOpenTvVisibility;
    }

    public int getOpenTvColor() {
        return mOpenTvColor;
    }

    public String getOpenTvCloseTimeString() {
        return mOpenTvCloseTimeString;
    }

    public int getWorkmateTvVisibility() {
        return mWorkmateTvVisibility;
    }

    public int getWorkmateIvVisibility() {
        return mWorkmateIvVisibility;
    }

    public void setOpeningHoursAvailable(boolean openingHoursAvailable) {
        mOpeningHoursAvailable = openingHoursAvailable;
    }

    public HashMap<Integer, List<OpenPeriod>> getOpeningHours() {
        return mOpeningHours;
    }

    public boolean isOpeningHoursAvailable() {
        return mOpeningHoursAvailable;
    }

    public int getDistanceFromUser() {
        return mDistanceFromUser;
    }

    public void setDistanceFromUser(int distanceFromUser) {
        mDistanceFromUser = distanceFromUser;
    }

    public void setDistanceTvVisibility(int distanceTvVisibility) {
        mDistanceTvVisibility = distanceTvVisibility;
    }

    public void setStar1IvVisibility(int star1IvVisibility) {
        mStar1IvVisibility = star1IvVisibility;
    }

    public void setStar2IvVisibility(int star2IvVisibility) {
        mStar2IvVisibility = star2IvVisibility;
    }

    public void setStar3IvVisibility(int star3IvVisibility) {
        mStar3IvVisibility = star3IvVisibility;
    }

    public void setOpenTvString(int openTvString) {
        mOpenTvString = openTvString;
    }

    public void setOpenTvCloseTimeString(String openTvCloseTimeString) {
        mOpenTvCloseTimeString = openTvCloseTimeString;
    }

    public void setOpenTvVisibility(int openTvVisibility) {
        mOpenTvVisibility = openTvVisibility;
    }

    public void setOpenTvColor(int openTvColor) {
        mOpenTvColor = openTvColor;
    }

    public void setWorkmateTvVisibility(int workmateTvVisibility) {
        mWorkmateTvVisibility = workmateTvVisibility;
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

    public void setMarkerOptionIconResource(int markerOptionIconResource) {
        mMarkerOptionIconResource = markerOptionIconResource;
    }

    public int getMarkerOptionIconResource() {
        return mMarkerOptionIconResource;
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

    public static class OpenPeriod{
        private final int openingHour;
        private final int openingMinute;
        private final int closingHour;
        private final int closingMinute;

        public OpenPeriod(int openingHour, int openingMinute, int closingHour, int closingMinute) {
            this.openingHour = openingHour;
            this.openingMinute = openingMinute;
            this.closingHour = closingHour;
            this.closingMinute = closingMinute;
        }

        public int getOpeningHour() {
            return openingHour;
        }

        public int getOpeningMinute() {
            return openingMinute;
        }

        public int getClosingHour() {
            return closingHour;
        }

        public int getClosingMinute() {
            return closingMinute;
        }
    }
}
