package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, ActivityCompat.OnRequestPermissionsResultCallback, EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 16;
    private static final String RESTAURANT = "restaurant";

    private SharedViewModel mSharedViewModel;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    private Boolean mPermissionDenied = false;

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

        mSharedViewModel.getPlaceListLiveData().observe(this, this::getGooglePlaceResults);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMapViewBinding binding = FragmentMapViewBinding.inflate(getLayoutInflater());
        binding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());

        configureMaps();
        configureLocation();

        return binding.getRoot();
    }

    private void configureMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void configureLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getSavedData();
        enableLocation();
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

    private void requestFocusToLocation(){
        if (mPermissionDenied && EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), Collections.singletonList(ACCESS_FINE_LOCATION))) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if(EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            focusToLocation();
        } else {
            requestPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void focusToLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                focusCamera(location.getLatitude(), location.getLongitude(), DEFAULT_ZOOM, true);
                getNearbyPlaces(location.getLatitude() + "," + location.getLongitude());
            }
        });
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

    private void getNearbyPlaces(String latlng) {
        mSharedViewModel.getNearbyPlaces(latlng, String.valueOf(getMapSize()), RESTAURANT);
    }

    @Override
    public void onCameraIdle() {
        fetchPlacesAtCameraPosition();
    }

    private void fetchPlacesAtCameraPosition(){
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        getNearbyPlaces(lat + "," + lng);
    }

    private void getGooglePlaceResults(List<Place> placeList) {
        if(mMap!=null) {
            for (Place place :placeList) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(place.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_normal));
               mMap.addMarker(markerOptions);
            }
        }
    }

    // ---------------
    // Permissions
    // ---------------

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private void enableLocation() {
        if (EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
            mMap.setMyLocationEnabled(true);

            if(!mSharedViewModel.isMapViewDataSet()) {
                focusToLocation();
            }
        } else {
            requestPermission();
        }
    }

    private void requestPermission(){
        EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), RC_LOCATION, ACCESS_FINE_LOCATION);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        mPermissionDenied = false;
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        mPermissionDenied = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE && EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
            enableLocation();
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
    }


    // ---------------
    // Utils
    // ---------------

    private double getMapSize(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, visibleRegion.nearRight);
    }
}