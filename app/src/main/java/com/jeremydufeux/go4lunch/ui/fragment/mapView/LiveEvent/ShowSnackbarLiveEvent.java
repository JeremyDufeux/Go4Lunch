package com.jeremydufeux.go4lunch.ui.fragment.mapView.LiveEvent;

import com.jeremydufeux.go4lunch.utils.LiveEvent;

public class ShowSnackbarLiveEvent implements LiveEvent {
    private int  mStingId;

    public ShowSnackbarLiveEvent(int stingId) {
        mStingId = stingId;
    }

    public int getStingId() {
        return mStingId;
    }
}
