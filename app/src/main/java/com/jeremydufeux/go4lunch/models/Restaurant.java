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
    private final HashMap<Integer, List<OpenPeriod>>  mOpeningHours;
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

        mOpeningHours = new HashMap<>();
        for(int i = 0; i < Calendar.DAY_OF_WEEK; i++){
            List<OpenPeriod> dayHours = new ArrayList<>();
            mOpeningHours.put(i, dayHours);
        }
    }

    public void calculateDistanceFromUser(Location location) {
        if(location != null) {
            mMeterDistanceFromUser = (int)location.distanceTo(mLocation) + "m";
            mDistanceTvVisibility = View.VISIBLE;
        } else {
            mDistanceTvVisibility = View.INVISIBLE;
        }
    }

    public void determineOpening() {
        mOpenTvColor = R.color.grey;
        if(mOpeningHoursAvailable) {
            mOpenTvCloseTimeString = getClosingSoonTime();
            if (mOpenNow) {
                if (!mOpenTvCloseTimeString.isEmpty() && !mAlwaysOpen) {
                    mOpenTvString = R.string.open_until;
                } else {
                    mOpenTvString = R.string.open_now;
                }
            } else {
                mOpenTvString = R.string.closed;
                mOpenTvColor = R.color.red;
            }
            mOpenTvVisibility = View.VISIBLE;
        } else {
            mOpenTvString = R.string.no_open_hours;
            mOpenTvVisibility = View.INVISIBLE;
        }
    }

    // If closing soon, return the closing time, else return an empty String
    public String getClosingSoonTime(){
        Calendar nowCal = Calendar.getInstance();

        for(OpenPeriod period : Objects.requireNonNull(mOpeningHours.get(nowCal.get(Calendar.DAY_OF_WEEK) - 1))){
            Calendar openCal = Calendar.getInstance();
            openCal.set(Calendar.HOUR_OF_DAY, period.getOpeningHour());
            openCal.set(Calendar.MINUTE, period.getOpeningMinute());
            openCal.set(Calendar.ZONE_OFFSET, mUtcOffset);

            Calendar closeCal = Calendar.getInstance();
            closeCal.set(Calendar.HOUR_OF_DAY, period.getClosingHour());
            closeCal.set(Calendar.MINUTE, period.getClosingMinute());
            closeCal.set(Calendar.ZONE_OFFSET, mUtcOffset);

            if( period.getClosingHour() == 0 && period.getClosingMinute() == 0){
                closeCal.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (nowCal.after(openCal) && nowCal.before(closeCal)) {
                nowCal.add(Calendar.HOUR_OF_DAY, 1);

                if(nowCal.after(closeCal)){
                    closeCal.set(Calendar.ZONE_OFFSET, nowCal.getTimeZone().getRawOffset());
                    return DateFormat.getTimeInstance(DateFormat.SHORT).format(closeCal.getTime());
                }
            }
        }
        return "";
    }

    public void determineWorkmatesViewVisibility(){
        if(mWorkmatesInterestedAmount > 0){
            mWorkmateTvVisibility = View.VISIBLE;
            mWorkmateIvVisibility = View.VISIBLE;
        } else {
            mWorkmateTvVisibility = View.INVISIBLE;
            mWorkmateIvVisibility = View.INVISIBLE;
        }
    }

    public void setAlwaysOpen(boolean alwaysOpen) {
        mOpeningHoursAvailable = true;
        mAlwaysOpen = alwaysOpen;
    }

    public void addOpeningHours(int dayOfWeek, int openingHour, int openingMinute, int closingHour, int closingMinute) {
        mOpeningHoursAvailable = true;
        Objects.requireNonNull(mOpeningHours.get(dayOfWeek)).add(new OpenPeriod(openingHour, openingMinute, closingHour, closingMinute));
    }

    public void setRating(float rating) {
        mRating = rating;

        if(mRating > 0) {
            mStar1IvVisibility = View.VISIBLE;
        } else {
            mStar1IvVisibility = View.INVISIBLE;
        }
        if(mRating > 1.66) {
            mStar2IvVisibility = View.VISIBLE;
        } else {
            mStar2IvVisibility = View.INVISIBLE;
        }
        if(mRating > 3.33) {
            mStar3IvVisibility = View.VISIBLE;
        } else {
            mStar3IvVisibility = View.INVISIBLE;
        }
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

    private static class OpenPeriod{
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
