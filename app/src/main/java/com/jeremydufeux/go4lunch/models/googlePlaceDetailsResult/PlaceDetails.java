
package com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceDetails {

    @SerializedName("address_components")
    @Expose
    private List<AddressComponent> addressComponents = null;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("utc_offset")
    @Expose
    private Integer UtcOffset;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("geometry")
    @Expose
    private PlaceDetailsGeometry mPlaceDetailsGeometry;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;

    public PlaceDetails() {
        mPlaceDetailsGeometry = new PlaceDetailsGeometry();
    }

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public Integer getUtcOffset() {
        return UtcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        UtcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public PlaceDetailsGeometry getPlaceDetailsGeometry() {
        return mPlaceDetailsGeometry;
    }

    public void setPlaceDetailsGeometry(PlaceDetailsGeometry placeDetailsGeometry) {
        this.mPlaceDetailsGeometry = placeDetailsGeometry;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
