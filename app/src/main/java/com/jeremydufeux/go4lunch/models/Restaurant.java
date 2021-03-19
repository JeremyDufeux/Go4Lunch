package com.jeremydufeux.go4lunch.models;

import android.location.Location;
import android.util.Log;

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
    private String mPhotoReference;
    private String mPhoneNumber;
    private String mWebsite;
    private String mPhotoUrl;
    private float mRating;
    private int mWorkmatesInterested;
    private boolean isOpenNow;
    // The HashMap represent the week, the key is for the day of the week: 0 for monday, 6 for sunday,
    // The list in value contain period of time where the place is open during the day, represented by the nested class OpenPeriod
    private final HashMap<Integer, List<OpenPeriod>>  mOpeningHours;
    private int mUtcOffset;
    private LatLng mLatlng;
    private Location mLocation;
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;
    private boolean mOpeningHoursAvailable;
    private boolean mAlwaysOpen;

    public Restaurant(String placeId, String name, Double lat, Double lng) {
        mUId = placeId;
        mName = name;

        mLatlng = new LatLng(lat,lng );
        mLocation = new Location("");
        mLocation.setLatitude(lat);
        mLocation.setLongitude(lng);

        mMarkerOptions = new MarkerOptions()
                .position(mLatlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_normal));

        mOpeningHours = new HashMap<>();

        for(int i = 0; i < Calendar.DAY_OF_WEEK; i++){
            List<OpenPeriod> dayHours = new ArrayList<>();
            mOpeningHours.put(i, dayHours);
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

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getWorkmatesInterested() {
        return mWorkmatesInterested;
    }

    public void setWorkmatesInterested(int workmatesInterested) {
        mWorkmatesInterested = workmatesInterested;
    }

    public boolean isOpeningHoursAvailable() {
        return mOpeningHoursAvailable;
    }

    public boolean isAlwaysOpen() {
        return mAlwaysOpen;
    }

    public void setAlwaysOpen(boolean alwaysOpen) {
        mOpeningHoursAvailable = true;
        mAlwaysOpen = alwaysOpen;
    }

    public void addOpeningHours(int dayOfWeek, int openingHour, int openingMinute, int closingHour, int closingMinute) {
        mOpeningHoursAvailable = true;
        Objects.requireNonNull(mOpeningHours.get(dayOfWeek)).add(new OpenPeriod(openingHour, openingMinute, closingHour, closingMinute));
    }

    public boolean isOpenNow() {
        return isOpenNow;
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

    public void setOpenNow(boolean openNow) {
        isOpenNow = openNow;
    }

    public int getUtcOffset() {
        return mUtcOffset;
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

    public String getPhotoReference() {
        return mPhotoReference;
    }

    public void setPhotoReference(String photoReference) {
        mPhotoReference = photoReference;
    }

    public LatLng getLatlng() {
        return mLatlng;
    }

    public void setLatlng(LatLng latlng) {
        mLatlng = latlng;
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

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        mMarkerOptions = markerOptions;
    }

    private static class OpenPeriod{
        private int openingHour;
        private int openingMinute;
        private int closingHour;
        private int closingMinute;

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
