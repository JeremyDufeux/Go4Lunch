package com.jeremydufeux.go4lunch.utils.liveEvent;

import com.google.android.gms.maps.model.LatLng;

public class FocusCameraLiveEvent implements LiveEvent {
    private final LatLng mLatLng;
    private final float mZoom;
    private final boolean mAnimate;

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
