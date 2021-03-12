package com.jeremydufeux.go4lunch.models;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.placeDetailsResult.OpeningHours;

public class Place {
    private final String mUId;
    private String mName;
    private String mAddress;
    private String mPhotoReference;
    private String mPhoneNumber;
    private String mWebsite;
    private String mPhotoUrl;
    private float mRating;
    private int mWorkmatesInterested;
    private OpeningHours mOpeningHours;
    private LatLng mLatlng;
    private Location mLocation;
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;

    public Place(String placeId, String name, Double lat, Double lng) {
        mUId = placeId;
        mName = name;

        mLatlng = new LatLng(lat,lng );
        mLocation = new Location("");
        mLocation.setLatitude(lat);
        mLocation.setLongitude(lng);

        mMarkerOptions = new MarkerOptions()
                .title(name)
                .position(mLatlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_normal));
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
}
