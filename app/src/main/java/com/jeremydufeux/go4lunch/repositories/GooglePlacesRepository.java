    package com.jeremydufeux.go4lunch.repositories;

import android.util.Log;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class GooglePlacesRepository {

    private static final String MAP_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=800";
    private static final String GEOAPIFY_PHOTO_URL = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=600&height=400&center=lonlat:%s,%s&zoom=17&marker=lonlat:%s,%s;color:%%23ff5721;size:xx-large&apiKey=%s";

    private String mNextPageToken;

    public Observable<List<Restaurant>> getNearbyPlaces(String latlng, String radius, String type){
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, radius, type)
                .map(this::getPlacesFromResults)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<List<Restaurant>> getNextPageNearbyPlaces() {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNextPageNearbyPlaces(BuildConfig.MAPS_API_KEY, mNextPageToken)
                .map(this::getPlacesFromResults)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<Restaurant> getDetailsForPlaceId(String placeId) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        String fields = "place_id,"
                + "name,"
                + "address_component,"
                + "vicinity,"
                + "geometry,"
                + "opening_hours,"
                + "utc_offset,"
                + "photo,"
                + "international_phone_number,"
                + "website,"
                + "rating";

        return placesService.fetchDetailsForPlaceId(BuildConfig.MAPS_API_KEY, placeId, fields)
                .map(this::getDetailsFromResults)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    private List<Restaurant> getPlacesFromResults(PlaceSearchResults results){
        List<Restaurant> restaurantList = new ArrayList<>();

        for(com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch placeSearch : results.getPlaceSearches()){
            if(placeSearch.getBusinessStatus()!= null && placeSearch.getBusinessStatus().equals("OPERATIONAL")) {

                double lat = placeSearch.getGeometry().getLocation().getLat();
                double lng = placeSearch.getGeometry().getLocation().getLng();

                Restaurant restaurant = new Restaurant(placeSearch.getPlaceId(), placeSearch.getName(), lat, lng);

                restaurantList.add(restaurant);
            }
        }
        if(results.getNextPageToken()!=null && !results.getNextPageToken().isEmpty()){
            mNextPageToken = results.getNextPageToken();
        } else {
            mNextPageToken = "";
        }

        return restaurantList;
    }

    private Restaurant getDetailsFromResults(PlaceDetailsResults results){
        PlaceDetails placeDetail = results.getPlaceDetails();

        double lat = placeDetail.getGeometry().getLocation().getLat();
        double lng = placeDetail.getGeometry().getLocation().getLng();

        Restaurant restaurant = new Restaurant(placeDetail.getPlaceId(), placeDetail.getName(), lat, lng);

        String address = getAddressFromAddressComponents(placeDetail.getAddressComponents());
        if(address.isEmpty()){
            address = placeDetail.getVicinity();
        }
        restaurant.setAddress(address);

        if(placeDetail.getOpeningHours() != null) {
            restaurant.setOpenNow(placeDetail.getOpeningHours().getOpenNow());
            restaurant.setUtcOffset(placeDetail.getUtcOffset()*60000);

            if(placeDetail.getOpeningHours().getPeriods() != null) {

                for (Period period : placeDetail.getOpeningHours().getPeriods()) {

                    if(period.getOpen().getDay() == 0 && period.getOpen().getTime().equals("0000")){
                        restaurant.setAlwaysOpen(true);
                    } else {
                        restaurant.addOpeningHours(period.getOpen().getDay(),
                                Integer.parseInt(period.getOpen().getTime().substring(0, 2)),
                                Integer.parseInt(period.getOpen().getTime().substring(2, 4)),
                                Integer.parseInt(period.getClose().getTime().substring(0, 2)),
                                Integer.parseInt(period.getClose().getTime().substring(2, 4))
                        );
                    }
                }
            }
        }

        if(placeDetail.getPhotos() != null){
            restaurant.setPhotoUrl(getUrlFromReference(placeDetail.getPhotos().get(0).getPhotoReference()));
        } else {
            restaurant.setPhotoUrl(getUrlFromGeoapify(lat,lng));
        }

        restaurant.setPhoneNumber(placeDetail.getInternationalPhoneNumber());
        restaurant.setWebsite(placeDetail.getWebsite());
        restaurant.setRating(placeDetail.getRating());

        return restaurant;
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
