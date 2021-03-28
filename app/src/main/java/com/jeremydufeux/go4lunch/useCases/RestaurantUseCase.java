package com.jeremydufeux.go4lunch.useCases;

import android.util.Log;

import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.AddressComponent;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.Period;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetails;
import com.jeremydufeux.go4lunch.models.googlePlaceDetailsResult.PlaceDetailsResults;
import com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearchResults;
import com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository;
import com.jeremydufeux.go4lunch.repositories.RestaurantRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RestaurantUseCase{
    private static final String MAP_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=%s&maxwidth=800";
    private static final String GEOAPIFY_PHOTO_URL = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=600&height=400&center=lonlat:%s,%s&zoom=17&marker=lonlat:%s,%s;color:%%23ff5721;size:xx-large&apiKey=%s";

    private final GooglePlacesRepository mGooglePlacesRepository;
    private final RestaurantRepository mRestaurantRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public RestaurantUseCase(GooglePlacesRepository googlePlacesRepository, RestaurantRepository restaurantRepository) {
        mGooglePlacesRepository = googlePlacesRepository;
        mRestaurantRepository = restaurantRepository;
    }

    public void getNearbyPlaces(double latitude, double longitude, double radius) {
        mDisposable.add(mGooglePlacesRepository.getNearbyPlaces(latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .subscribeWith(receiptResultFromNearbyPlaces()));
    }

    public void getDetailsForPlaceId(String placeId){
        mDisposable.add(mGooglePlacesRepository.getDetailsForPlaceId(placeId)
                .subscribeOn(Schedulers.io())
                .subscribeWith(receiptResultFromPlacesDetails()));
    }

    private DisposableObserver<PlaceSearchResults> receiptResultFromNearbyPlaces() {
        return new DisposableObserver<PlaceSearchResults>() {
            @Override
            public void onNext(@NonNull PlaceSearchResults results) {

                HashMap<String, Restaurant> restaurantList = new HashMap<>();

                for(com.jeremydufeux.go4lunch.models.googlePlaceResult.PlaceSearch placeSearch : results.getPlaceSearches()){
                    if(placeSearch.getBusinessStatus()!= null && placeSearch.getBusinessStatus().equals("OPERATIONAL")) {

                        String uId = placeSearch.getPlaceId();

                        double lat = placeSearch.getGeometry().getLocation().getLat();
                        double lng = placeSearch.getGeometry().getLocation().getLng();

                        Restaurant restaurant = new Restaurant(uId, placeSearch.getName(), lat, lng);

                        restaurantList.put(uId, restaurant);

                        getDetailsForPlaceId(uId);
                    }
                }

                mRestaurantRepository.replaceRestaurantList(restaurantList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Debug", "onError getNearbyPlaces " + e.toString());
            }
            @Override
            public void onComplete() {
            }
        };
    }

    private DisposableObserver<PlaceDetailsResults> receiptResultFromPlacesDetails() {
        return new DisposableObserver<PlaceDetailsResults>() {
            @Override
            public void onNext(@NonNull PlaceDetailsResults results) {
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

                mRestaurantRepository.addRestaurantDetails(restaurant);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Debug", "onError getNearbyPlaces " + e.toString());
            }
            @Override
            public void onComplete() {
            }
        };
    }

    public Observable<HashMap<String, Restaurant>> observeRestaurantList(){
        return mRestaurantRepository.observeRestaurantList();
    }

    public void clearDisposable(){
        mDisposable.clear();
    }

    // ------------
    // Utils
    // ------------

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
