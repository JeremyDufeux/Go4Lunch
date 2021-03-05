package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentMapViewBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.GooglePlaceResults;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.Result;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, ActivityCompat.OnRequestPermissionsResultCallback, EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION = 1;

    private MapViewFragmentViewModel mViewModel;

    private FragmentMapViewBinding mBinding;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    private Boolean mPermissionDenied = false;

    public MapViewFragment() {
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(MapViewFragmentViewModel.class);
        mViewModel.getGooglePlaceList().observe(this, this::getGooglePlaceResults);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        mBinding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());

        configureMaps();
        configureLocation();

        return mBinding.getRoot();
    }

    private void configureMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment_map);
        mapFragment.getMapAsync(this);
    }

    private void configureLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        enableLocation();
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private void enableLocation() {
        if (EasyPermissions.hasPermissions(getActivity(), ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                focusToLocation();
            }
        } else {
            requestPermission();
        }
    }

    private void requestPermission(){
        EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), RC_LOCATION, ACCESS_FINE_LOCATION);
    }

    private void requestFocusToLocation(){
        if (mPermissionDenied && EasyPermissions.somePermissionPermanentlyDenied(getActivity(), Collections.singletonList(ACCESS_FINE_LOCATION))) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if(EasyPermissions.hasPermissions(getActivity(), ACCESS_FINE_LOCATION)) {
            focusToLocation();
        } else {
            requestPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void focusToLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                focusCamera(location.getLatitude(), location.getLongitude(), true);
                fetchNearbyPlaces(location.getLatitude() + "," + location.getLongitude());
            }
        });
    }

    private void focusCamera(double lat, double lng, boolean animCamera){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if(animCamera){
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void fetchNearbyPlaces(String latlng) {
        mViewModel.fetchNearbyPlaces(latlng, String.valueOf(getMapSize()), "restaurant");
    }

    private void getGooglePlaceResults(GooglePlaceResults googlePlacesResults) {
        for(Result place : googlePlacesResults.getResults()){
            LatLng latLng = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(place.getName())
            );
        }
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

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE && EasyPermissions.hasPermissions(getActivity(), ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
            enableLocation();
        }
    }

    @Override
    public void onCameraIdle() {
        fetchPlacesAtCameraPosition();
    }

    private void fetchPlacesAtCameraPosition(){
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        fetchNearbyPlaces(lat + "," + lng);
    }

    private double getMapSize(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, mMap.getCameraPosition().target);
    }
}