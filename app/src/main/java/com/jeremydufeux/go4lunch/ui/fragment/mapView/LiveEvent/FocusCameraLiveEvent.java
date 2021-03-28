package com.jeremydufeux.go4lunch.ui.fragment.mapView.LiveEvent;

import com.google.android.gms.maps.model.LatLng;
import com.jeremydufeux.go4lunch.utils.LiveEvent;

public class FocusCameraLiveEvent implements LiveEvent {
    private LatLng mLatLng;
    private float mZoom;
    private boolean mAnimate;

    public FocusCameraLiveEvent(LatLng latLng, float zoom, boolean animate) {
        mLatLng = latLng;
        mZoom = zoom;
        mAnimate = animate;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public float getZoom() {
        return mZoom;
    }

    public boolean isAnimate() {
        return mAnimate;
    }
}
