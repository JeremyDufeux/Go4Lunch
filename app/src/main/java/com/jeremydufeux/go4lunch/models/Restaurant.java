package com.jeremydufeux.go4lunch.models;

import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremydufeux.go4lunch.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Restaurant {
    private final String mUId;
    private String mName;
    private String mAddress;
    private String mPhoneNumber;
    private String mWebsite;
    private String mPhotoUrl;
    private float mRating;
    private int mWorkmatesInterestedAmount;

    // For Opening
    /** The HashMap represent the week, the key is for the day of the week: 0 for monday, 6 for sunday,
     The list in value contain period of time where the place is open during the day, represented by the nested class OpenPeriod  **/
    private HashMap<Integer, List<OpenPeriod>>  mOpeningHours;
    private boolean mOpenNow;
    private boolean mOpeningHoursAvailable;
    private boolean mAlwaysOpen;
    private int mUtcOffset;

    private Location mLocation;
    private String mMeterDistanceFromUser;

    private Marker mMarker;
    private final MarkerOptions mMarkerOptions;

    // For ListViewPlacesAdapter
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

    public Restaurant(String placeId, String name, Double lat, Double lng) {
        mUId = placeId;
        mName = name;

        mLocation = new Location("");
        mLocation.setLatitude(lat);
        mLocation.setLongitude(lng);

        mMarkerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_normal));
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

    public int getWorkmatesInterestedAmount() {
        return mWorkmatesInterestedAmount;
    }

    public void setWorkmatesInterestedAmount(int workmatesInterestedAmount) {
        mWorkmatesInterestedAmount = workmatesInterestedAmount;
    }

    public void setOpenNow(boolean openNow) {
        mOpenNow = openNow;
    }

    public void setUtcOffset(int utcOffset) {
        this.mUtcOffset = utcOffset;
    }

    public boolean isOpenNow() {
        return mOpenNow;
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

    public String getMeterDistanceFromUser() {
        return mMeterDistanceFromUser;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
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

    public void setMeterDistanceFromUser(String meterDistanceFromUser) {
        mMeterDistanceFromUser = meterDistanceFromUser;
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
