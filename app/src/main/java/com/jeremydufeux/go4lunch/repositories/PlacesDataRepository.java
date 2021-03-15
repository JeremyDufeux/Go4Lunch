package com.jeremydufeux.go4lunch.repositories;

import android.util.Pair;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.api.PlacesService;
import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.models.placeDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.placeDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.placeDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.placeDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.placeResult.PlaceSearch;
import com.jeremydufeux.go4lunch.models.placeResult.PlaceSearchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class PlacesDataRepository {

    public static final String MAP_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=800";
    public static final String GEOAPIFY_PHOTO_URL = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=600&height=400&center=lonlat:%s,%s&zoom=17&marker=lonlat:%s,%s;color:%%23ff5721;size:xx-large&apiKey=%s";

    public Observable<Pair<List<Place>, String>> getNearbyPlaces(String latlng, String radius, String type){
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNearbyPlaces(BuildConfig.MAPS_API_KEY, latlng, radius, type)
                .map(this::getPlacesFromResults)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<Pair<List<Place>, String>> getNextPageNearbyPlaces(String pageToken) {
        PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

        return placesService.fetchNextPageNearbyPlaces(BuildConfig.MAPS_API_KEY, pageToken)
                .map(this::getPlacesFromResults)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<Place> getDetailsForPlaceId(String placeId) {
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

    private Pair<List<Place>, String> getPlacesFromResults(PlaceSearchResults results){
        List<Place> placeList = new ArrayList<>();

        for(PlaceSearch placeSearch : results.getPlaceSearches()){
            if(placeSearch.getBusinessStatus()!= null) {
                if (placeSearch.getBusinessStatus().equals("OPERATIONAL")) {

                    double lat = placeSearch.getGeometry().getLocation().getLat();
                    double lng = placeSearch.getGeometry().getLocation().getLng();

                    Place place = new Place(placeSearch.getPlaceId(), placeSearch.getName(), lat, lng);

                    placeList.add(place);
                }
            }
        }
        String nextPageToken = results.getNextPageToken();
        return new Pair<>(placeList, nextPageToken);
    }

    private Place getDetailsFromResults(PlaceDetailsResults results){
        PlaceDetails placeDetail = results.getPlaceDetails();

        double lat = placeDetail.getGeometry().getLocation().getLat();
        double lng = placeDetail.getGeometry().getLocation().getLng();

        Place place = new Place(placeDetail.getPlaceId(), placeDetail.getName(), lat, lng);

        String address = getAddressFromAddressComponents(placeDetail.getAddressComponents());
        if(address.isEmpty()){
            address = placeDetail.getVicinity();
        }
        place.setAddress(address);

        if(placeDetail.getOpeningHours() != null) {
            place.setOpenNow(placeDetail.getOpeningHours().getOpenNow());
            place.setUtcOffset(placeDetail.getUtcOffset()*60000);

            if(placeDetail.getOpeningHours().getPeriods() != null) {
                for (Period period : placeDetail.getOpeningHours().getPeriods()) {
                    place.addOpeningHours(period.getOpen().getDay(),
                            Integer.parseInt(period.getOpen().getTime().substring(0, 2)),
                            Integer.parseInt(period.getOpen().getTime().substring(2, 4)),
                            Integer.parseInt(period.getClose().getTime().substring(0, 2)),
                            Integer.parseInt(period.getClose().getTime().substring(2, 4))
                            );
                }
            }
        }

        if(placeDetail.getPhotos() != null){
            place.setPhotoUrl(getUrlFromReference(placeDetail.getPhotos().get(0).getPhotoReference()));
        } else {
            place.setPhotoUrl(getUrlFromGeoapify(lat,lng));
        }

        place.setPhoneNumber(placeDetail.getInternationalPhoneNumber());
        place.setWebsite(placeDetail.getWebsite());
        place.setRating(placeDetail.getRating());

        return place;
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
