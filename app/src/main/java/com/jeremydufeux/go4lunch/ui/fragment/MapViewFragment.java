package com.jeremydufeux.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentMapViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION = 1;

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
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        mBinding.mapViewFragmentLocationBtn.setOnClickListener(v -> requestFocusToLocation());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_fragment_map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return mBinding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), RC_LOCATION, ACCESS_FINE_LOCATION);
        }
    }

    private void requestFocusToLocation(){
        if (mPermissionDenied && EasyPermissions.somePermissionPermanentlyDenied(getActivity(), Collections.singletonList(ACCESS_FINE_LOCATION))) {
            new AppSettingsDialog.Builder(this).build().show();
        } else if(EasyPermissions.hasPermissions(getActivity(), ACCESS_FINE_LOCATION)) {
            focusToLocation();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), RC_LOCATION, ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void focusToLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mMap.animateCamera(cameraUpdate);
            }
        });
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
}