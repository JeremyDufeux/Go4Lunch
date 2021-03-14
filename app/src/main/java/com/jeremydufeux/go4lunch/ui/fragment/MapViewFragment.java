package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentMapViewBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.jeremydufeux.go4lunch.ui.MainActivity.PERMS_RC_LOCATION;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM_VALUE = 16;
    private static final float LIMIT_ZOOM_VALUE = 14.6f;
    private static final String PLACE_TYPE_RESTAURANT = "restaurant";

    private SharedViewModel mSharedViewModel;

    private GoogleMap mMap;
    private Location mLocation;

    private List<Place> mPlaces = new ArrayList<>();
    private Boolean mMapReady = false;

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

        mSharedViewModel.observePlaceList().observe(this, this::onPlaceResultsChanged);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMapViewBinding binding = FragmentMapViewBinding.inflate(getLayoutInflater());
        binding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());

        configureMaps();

        return binding.getRoot();
    }

    private void configureMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this::onCameraIdle);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMapReady = true;
        mSharedViewModel.observeLocationPermissionGranted().observe(this, this::enableLocation);
        getSavedData();
        updateMap();
    }

    private void getSavedData() {
        if(mSharedViewModel.isMapViewDataSet()){
            focusCamera(mSharedViewModel.getMapViewCameraLatitude(),
                    mSharedViewModel.getMapViewCameraLongitude(),
                    mSharedViewModel.getMapViewCameraZoom(),
                    false);
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
        }
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
        focusCamera(mLocation.getLatitude(), mLocation.getLongitude(), DEFAULT_ZOOM_VALUE, true);
        getNearbyPlaces(mLocation.getLatitude() + "," + mLocation.getLongitude());
    }

    private void focusCamera(double lat, double lng, float zoom, boolean animCamera){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        if(animCamera){
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    // ---------------
    // Places
    // ---------------

    public void onCameraIdle() {
        fetchPlacesAtCameraPosition();
        updateMap();
    }

    private void fetchPlacesAtCameraPosition(){
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        getNearbyPlaces(lat + "," + lng);
    }

    private void getNearbyPlaces(String latlng) {
        mSharedViewModel.getNearbyPlaces(latlng, String.valueOf(getVisibleRegionRadius()), PLACE_TYPE_RESTAURANT);
    }

    private void onPlaceResultsChanged(List<Place> placeList) {
        mPlaces = placeList;
        updateMap();
    }

    private void updateMap() {
        if (mMapReady && mPlaces != null) {
            if (mMap.getCameraPosition().zoom > LIMIT_ZOOM_VALUE) {
                addMarkersInViewport();
            } else {
                hideAllMarkers();
            }
        }
    }

    private void addMarkersInViewport(){
        for (Place place : mPlaces) {
            if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(place.getLatlng())) {
                if (place.getMarker() == null) {
                    place.setMarker(mMap.addMarker(place.getMarkerOptions()));
                }
            }
        }
    }

    private void hideAllMarkers(){
        mMap.clear();
        for (Place place : mPlaces) {
            place.setMarker(null);
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
        mSharedViewModel.setMapViewCameraLatitude(mMap.getCameraPosition().target.latitude);
        mSharedViewModel.setMapViewCameraLongitude(mMap.getCameraPosition().target.longitude);
        mSharedViewModel.setMapViewCameraZoom(mMap.getCameraPosition().zoom);
        mSharedViewModel.setMapViewDataSet(true);

        for (Place place : mPlaces) {
            place.setMarker(null);
        }
    }


    // ---------------
    // Utils
    // ---------------

    private double getVisibleRegionRadius(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, visibleRegion.nearRight);
    }
}