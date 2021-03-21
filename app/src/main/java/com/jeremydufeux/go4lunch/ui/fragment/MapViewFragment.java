package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.MainNavDirections;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentMapViewBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.jeremydufeux.go4lunch.ui.MainActivity.PERMS_RC_LOCATION;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM_VALUE = 16;
    private static final float LIMIT_ZOOM_VALUE = 14.6f;

    private SharedViewModel mSharedViewModel;
    private MapViewViewModel mMapViewViewModel;
    private FragmentMapViewBinding mBinding;

    private GoogleMap mMap;
    private Location mLocation;

    private HashMap<String, Restaurant> mRestaurants = new HashMap<>();
    private boolean mMapReady = false;
    private boolean mCanShowSearchButton = false;
    private boolean mFirstMove = true;
    private boolean mFetchPlacesAfterCameraIdle = false;
    private boolean mCanShowZoomSnackbar = false;

    // ---------------
    // Setup
    // ---------------

    public MapViewFragment() {
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModels();
    }

    private void configureViewModels() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mSharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel.class);
        mMapViewViewModel = new ViewModelProvider(this, viewModelFactory).get(MapViewViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        mBinding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());
        mBinding.mapViewFragmentSearchButton.setOnClickListener(v -> searchThisAreaAction());

        mMapViewViewModel.observeRestaurantList().observe(getViewLifecycleOwner(), this::onRestaurantListChanged);

        configureMaps();

        return mBinding.getRoot();
    }

    private void configureMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this::onCameraIdle);
        mMap.setOnMarkerClickListener(this::onMarkerClick);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMapReady = true;
        mSharedViewModel.observeLocationPermissionGranted().observe(getViewLifecycleOwner(), this::enableLocation);
        getSavedData();
        mCanShowSearchButton = false;
    }

    private boolean onMarkerClick(Marker marker) {
        MainNavDirections.ActionGlobalRestaurantDetailsFragment directions = MainNavDirections.actionGlobalRestaurantDetailsFragment();
        directions.setRestaurantId((String) Objects.requireNonNull(marker.getTag()));

        Navigation.findNavController(mBinding.getRoot()).navigate(directions);
        return false;
    }

    private void getSavedData() {
        if(mSharedViewModel.isMapViewDataSet()){
            focusCamera(mSharedViewModel.getMapViewCameraLatitude(),
                    mSharedViewModel.getMapViewCameraLongitude(),
                    mSharedViewModel.getMapViewCameraZoom(),
                    false);
            updateMap();
        }
    }

    // ---------------
    // Map interactions
    // ---------------

    @AfterPermissionGranted(PERMS_RC_LOCATION)
    private void requestFocusToLocation(){
        if (!EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            mSharedViewModel.setSystemSettingsDialogRequest(true);
        } else {
            focusToLocation();
            mCanShowSearchButton = true;
        }
    }

    private void searchThisAreaAction() {
        hideSearchButton();
        fetchPlacesAtCameraPosition();
    }

    private void onUserLocationChanged(Location location) {
        if (location != null) {
            mLocation = location;
            if(!mSharedViewModel.isMapViewDataSet()) {
                focusToLocation();
                mSharedViewModel.setMapViewDataSet(true);
            }
        }
    }

    private void focusToLocation() {
        if(mLocation!=null) {
            focusCamera(mLocation.getLatitude(), mLocation.getLongitude(), DEFAULT_ZOOM_VALUE, true);
            if (mFirstMove) {
                mFirstMove = false;
                mFetchPlacesAfterCameraIdle = true;
            }
        } else {
            showSnackBar(getString(R.string.position_unknown_for_now));
        }
    }

    private void focusCamera(double lat, double lng, float zoom, boolean animCamera){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        if(animCamera){
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
        mCanShowSearchButton = false;
    }

    // ---------------
    // Places
    // ---------------

    public void onCameraIdle() {
        if(mFetchPlacesAfterCameraIdle){
            fetchPlacesAtCameraPosition();
            mFetchPlacesAfterCameraIdle = false;
        }

        if(mMap.getCameraPosition().zoom > LIMIT_ZOOM_VALUE) {
            if(mCanShowSearchButton) {
                showSearchButton();
            } else {
                mCanShowSearchButton = true;
            }
            addMarkersInViewport();
            mCanShowZoomSnackbar = true;
        } else {
            hideSearchButton();
            hideAllMarkers();

            if(mCanShowZoomSnackbar) {
                showSnackBar(getString(R.string.zoom_to_see_restaurants));
                mCanShowZoomSnackbar = false;
            }
            mCanShowSearchButton = true;
        }
    }

    private void fetchPlacesAtCameraPosition(){
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        getNearbyPlaces(lat + "," + lng);
    }

    private void getNearbyPlaces(String latlng) {
        mMapViewViewModel.getNearbyPlaces(latlng, String.valueOf(getVisibleRegionRadius()));
    }

    private void onRestaurantListChanged(HashMap<String, Restaurant> restaurantList) {
        removeLastMarkers(restaurantList);
        mRestaurants = restaurantList;
        updateMap();
        if(restaurantList.size()==0){
            showSnackBar(getString(R.string.no_restaurants_found));
        }
    }

    private void removeLastMarkers(HashMap<String, Restaurant> restaurantList){
        for(Restaurant restaurant : mRestaurants.values()){
            if(!restaurantList.containsKey(restaurant.getUId())){
                restaurant.getMarker().remove();
            }
        }
    }

    private void updateMap() {
        if (mMapReady) {
            if (mMap.getCameraPosition().zoom > LIMIT_ZOOM_VALUE) {
                addMarkersInViewport();
                mCanShowZoomSnackbar = true;
            } else {
                if(mCanShowZoomSnackbar) {
                    showSnackBar(getString(R.string.zoom_to_see_restaurants));
                    mCanShowZoomSnackbar = false;
                }
                hideAllMarkers();
            }
        }
    }

    private void addMarkersInViewport(){
        for (Restaurant restaurant : mRestaurants.values()) {
            if (restaurant.getMarker() == null) {
                restaurant.setMarker(mMap.addMarker(restaurant.getMarkerOptions()));
                restaurant.getMarker().setTag(restaurant.getUId());
            }
        }
    }

    private void hideAllMarkers(){
        mMap.clear();
        for (Restaurant restaurant : mRestaurants.values()) {
            restaurant.setMarker(null);
        }
    }

    // ---------------
    // Permissions
    // ---------------
    @SuppressLint("MissingPermission")
    private void enableLocation(Boolean locationPermissionGranted) {
        if (mMap != null && locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mSharedViewModel.observeUserLocation().observe(this, this::onUserLocationChanged);
        }
    }

    // ---------------
    // Save Data
    // ---------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSharedViewModel.observeLocationPermissionGranted().removeObservers(this);
        mSharedViewModel.observeUserLocation().removeObservers(this);
        mMapViewViewModel.observeRestaurantList().removeObservers(this);

        mSharedViewModel.setMapViewCameraLatitude(mMap.getCameraPosition().target.latitude);
        mSharedViewModel.setMapViewCameraLongitude(mMap.getCameraPosition().target.longitude);
        mSharedViewModel.setMapViewCameraZoom(mMap.getCameraPosition().zoom);
        mSharedViewModel.setMapViewDataSet(true);

        for (Restaurant restaurant : mRestaurants.values()) {
            restaurant.setMarker(null);
        }
    }


    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(String message){
        Snackbar.make(mBinding.mapViewFragmentCoordinator, message, Snackbar.LENGTH_LONG).show();
    }

    private double getVisibleRegionRadius(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, visibleRegion.nearRight)/2;
    }

    private void showSearchButton(){
        mBinding.mapViewFragmentSearchButton.animate().alpha(1).setDuration(1000);
    }

    private void hideSearchButton(){
        mBinding.mapViewFragmentSearchButton.animate().alpha(0).setDuration(200);
    }
}