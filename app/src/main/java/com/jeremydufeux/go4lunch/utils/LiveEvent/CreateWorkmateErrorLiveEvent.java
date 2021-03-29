package com.jeremydufeux.go4lunch.utils.LiveEvent;

public class CreateWorkmateErrorLiveEvent implements LiveEvent {
    private final Exception mException;

    public CreateWorkmateErrorLiveEvent(Exception exception) {
        mException = exception;
    }

    public Exception getException() {
        return mException;
    }
}
