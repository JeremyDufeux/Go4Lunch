package com.jeremydufeux.go4lunch.utils.liveEvent;

public class ErrorLiveEvent implements LiveEvent {
    private final Exception mException;

    public ErrorLiveEvent(Exception exception) {
        mException = exception;
    }

    public Exception getException() {
        return mException;
    }
}
