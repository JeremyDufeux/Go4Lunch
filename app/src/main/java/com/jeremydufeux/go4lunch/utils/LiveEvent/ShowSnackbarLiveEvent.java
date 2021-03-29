package com.jeremydufeux.go4lunch.utils.LiveEvent;

public class ShowSnackbarLiveEvent implements LiveEvent {
    private final int  mStingId;

    public ShowSnackbarLiveEvent(int stingId) {
        mStingId = stingId;
    }

    public int getStingId() {
        return mStingId;
    }
}
