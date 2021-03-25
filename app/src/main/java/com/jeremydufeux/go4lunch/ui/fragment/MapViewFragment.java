package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends BaseFragment implements
        OnMapReadyCallback,
        EasyPermissions.PermissionCallbacks,
        MapViewViewModel.MapViewCallback {

    public static final int PERMS_RC_LOCATION = 1;
    public static final float DEFAULT_ZOOM_VALUE = 16;
    public static final float LIMIT_ZOOM_VALUE = 14.6f;

    private MapViewViewModel mViewModel;
    private FragmentMapViewBinding mBinding;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;

    private boolean mPermissionDenied = false;

    public HashMap<String, Restaurant> mRestaurantList = new HashMap<>();

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
        configureLocationClient();
    }

    private void configureViewModels() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(MapViewViewModel.class);
        mViewModel.setMapViewCallback(this);
        mViewModel.setCanShowSearchButton(false);
    }

    private void configureLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        mBinding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());
        mBinding.mapViewFragmentSearchButton.setOnClickListener(v -> searchThisAreaAction());

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

        enableLocation();
        configureRestaurantObserver();

        mViewModel.checkForSavedData();
    }

    private void configureRestaurantObserver(){
        mDisposable.add(mViewModel.onRestaurantListChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(onRestaurantListChanged())
        );
    }

    // ---------------
    // Map interactions
    // ---------------

    private void requestFocusToLocation(){
        if (EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            mViewModel.focusToLocation();
        } else {
            enableLocation();
        }
    }

    public void focusCamera(LatLng latLng, float zoom, boolean animCamera) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        if (animCamera) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void searchThisAreaAction() {
        hideSearchButton();
        mViewModel.getNearbyPlaces(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, getVisibleRegionRadius());
    }

    private void onCameraIdle() {
        mViewModel.onCameraIdle(mMap.getCameraPosition().target.latitude,
                mMap.getCameraPosition().target.longitude,
                mMap.getCameraPosition().zoom,
                getVisibleRegionRadius());
    }

    // ---------------
    // Places
    // ---------------

    private DisposableObserver<HashMap<String, Restaurant>> onRestaurantListChanged(){
        return new DisposableObserver<HashMap<String, Restaurant>>() {
            @Override
            public void onNext(@NonNull HashMap<String, Restaurant> restaurantList) {
                removeUnusedMarkers(restaurantList);
                mRestaurantList = restaurantList;
                if (mMap.getCameraPosition().zoom > LIMIT_ZOOM_VALUE) {
                    addMarkers();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Debug", "onError " + e.toString());
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void removeUnusedMarkers(HashMap<String, Restaurant> restaurantList) {
        for (Restaurant restaurant : mRestaurantList.values()) {
            if (!restaurantList.containsKey(restaurant.getUId())) {
                restaurant.getMarker().remove();
            }
        }
    }

    public void addMarkers(){
        for (Restaurant restaurant : mRestaurantList.values()) {
            restaurant.setMarker(mMap.addMarker(restaurant.getMarkerOptions()));
            restaurant.getMarker().setTag(restaurant.getUId());
        }
    }

    public void removeMarkers(){
        mMap.clear();
    }

    private boolean onMarkerClick(Marker marker) {
        MainNavDirections.ActionGlobalRestaurantDetailsFragment directions = MainNavDirections.actionGlobalRestaurantDetailsFragment();
        directions.setRestaurantId((String) Objects.requireNonNull(marker.getTag()));

        Navigation.findNavController(mBinding.getRoot()).navigate(directions);
        return false;
    }

    // ---------------
    // Location
    // ---------------

    private void enableLocation() {
        if (EasyPermissions.hasPermissions(requireActivity(), ACCESS_FINE_LOCATION)) {
            configureLocation();
        } else {
            if(mPermissionDenied){
                openSystemSettingsDialog();
            } else {
                requestPermission();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(PERMS_RC_LOCATION)
    private void configureLocation() {
        mPermissionDenied = false;
        mViewModel.setLocationPermissionGranted(true);
        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            mLocation = location;
            mViewModel.setLocation(mLocation);
        });

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                mLocation = locationResult.getLastLocation();
                mViewModel.setLocation(mLocation);
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    // ---------------
    // Permissions
    // ---------------

    private void requestPermission(){
        requestPermissions( new String[]{ACCESS_FINE_LOCATION}, PERMS_RC_LOCATION);
    }

    public void openSystemSettingsDialog() {
        new AppSettingsDialog.Builder(this).build().show();
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
        mDisposable.clear();

        mViewModel.saveCameraData(mMap.getCameraPosition().target.latitude,
                mMap.getCameraPosition().target.longitude,
                mMap.getCameraPosition().zoom);

    }

    // ---------------
    // Utils
    // ---------------

    private double getVisibleRegionRadius(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, visibleRegion.nearRight)/2;
    }

    public void showSnackBar(int stringId){
        Snackbar.make(mBinding.mapViewFragmentCoordinator, getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    public void showSearchButton(){
        mBinding.mapViewFragmentSearchButton.animate().alpha(1).setDuration(1000);
    }

    public void hideSearchButton(){
        mBinding.mapViewFragmentSearchButton.animate().alpha(0).setDuration(200);
    }
}