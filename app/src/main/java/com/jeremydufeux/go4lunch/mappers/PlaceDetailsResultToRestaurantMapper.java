package com.jeremydufeux.go4lunch.mappers;

import android.view.View;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class PlaceDetailsResultToRestaurantMapper implements Function<PlaceDetailsResults, Restaurant> {
    private static final String MAP_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=800";
    private static final String GEOAPIFY_PHOTO_URL = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=600&height=400&center=lonlat:%s,%s&zoom=17&marker=lonlat:%s,%s;color:%%23ff5721;size:xx-large&apiKey=%s";

    @Override
    public Restaurant apply(@NonNull PlaceDetailsResults results) {
        PlaceDetails placeDetail = results.getPlaceDetails();

        double lat = placeDetail.getGeometry().getLocation().getLat();
        double lng = placeDetail.getGeometry().getLocation().getLng();

        Restaurant restaurant = new Restaurant(placeDetail.getPlaceId(), placeDetail.getName(), lat, lng);

        setAddress(restaurant, placeDetail);
        setOpeningData(restaurant, placeDetail);
        setRating(restaurant, placeDetail);
        restaurant.setPhotoUrl(getPhotoUrl(placeDetail));
        restaurant.setPhoneNumber(placeDetail.getInternationalPhoneNumber());
        restaurant.setWebsite(placeDetail.getWebsite());

        return restaurant;
    }

    private void setAddress(Restaurant restaurant, PlaceDetails placeDetail) {
        String address = getAddressFromAddressComponents(placeDetail.getAddressComponents());
        if(address.isEmpty()){
            address = placeDetail.getVicinity();
        }
        restaurant.setAddress(address);
    }

    private void setOpeningData(Restaurant restaurant, PlaceDetails placeDetail) {
        if(placeDetail.getOpeningHours() != null) {
            restaurant.setOpenNow(placeDetail.getOpeningHours().getOpenNow());
            restaurant.setOpeningHoursAvailable(true);
            restaurant.setUtcOffset(placeDetail.getUtcOffset()*60000);

            if(placeDetail.getOpeningHours().getPeriods() != null) {

                HashMap<Integer, List<Restaurant.OpenPeriod>> openingHours = new HashMap<>();
                for(int i = 0; i < Calendar.DAY_OF_WEEK; i++){
                    List<Restaurant.OpenPeriod> dayHours = new ArrayList<>();
                    openingHours.put(i, dayHours);
                }

                for (Period period : placeDetail.getOpeningHours().getPeriods()) {
                    if(period.getOpen().getDay() == 0 && period.getOpen().getTime().equals("0000")){
                        restaurant.setAlwaysOpen(true);
                        return;
                    } else {
                        Restaurant.OpenPeriod openPeriod = new Restaurant.OpenPeriod(
                                Integer.parseInt(period.getOpen().getTime().substring(0, 2)),
                                Integer.parseInt(period.getOpen().getTime().substring(2, 4)),
                                Integer.parseInt(period.getClose().getTime().substring(0, 2)),
                                Integer.parseInt(period.getClose().getTime().substring(2, 4)));

                        Objects.requireNonNull(openingHours.get(period.getOpen().getDay())).add(openPeriod);
                    }
                }
                restaurant.setOpeningHours(openingHours);
            }
        }
    }

    private String getPhotoUrl(PlaceDetails placeDetail) {
        if(placeDetail.getPhotos() != null){
            return getUrlFromReference(placeDetail.getPhotos().get(0).getPhotoReference());
        } else {
            return getUrlFromGeoapify(placeDetail.getGeometry().getLocation().getLat(),placeDetail.getGeometry().getLocation().getLng());
        }
    }

    private void setRating(Restaurant restaurant, PlaceDetails placeDetail) {
        restaurant.setRating(placeDetail.getRating());

        if(restaurant.getRating() > 0) {
            restaurant.setStar1IvVisibility(View.VISIBLE);
        } else {
            restaurant.setStar1IvVisibility(View.INVISIBLE);
        }
        if(restaurant.getRating() > 1.66) {
            restaurant.setStar2IvVisibility(View.VISIBLE);
        } else {
            restaurant.setStar2IvVisibility(View.INVISIBLE);
        }
        if(restaurant.getRating() > 3.33) {
            restaurant.setStar3IvVisibility(View.VISIBLE);
        } else {
            restaurant.setStar3IvVisibility(View.INVISIBLE);
        }
    }

    private String getAddressFromAddressComponents(List<AddressComponent> addressComponents){
        String streetNumber = "";
        String route = "";
        for (int i = 0; i < addressComponents.size(); i++){
            if(addressComponents.get(i).getTypes().contains("street_number")){
                streetNumber = addressComponents.get(i).getLongName() + ", ";
            } else if(addressComponents.get(i).getTypes().contains("route")){
                route = addressComponents.get(i).getLongName();
            }
        }
        return streetNumber + route;
    }

    private String getUrlFromReference(String photoReference) {
        return String.format(MAP_PHOTO_URL, photoReference, BuildConfig.MAPS_API_KEY);
    }

    private String getUrlFromGeoapify(double lat, double lng) {
        return String.format(GEOAPIFY_PHOTO_URL, lng, lat, lng, lat, BuildConfig.GEOAPIFY_API_KEY);
    }
}
