package com.jeremydufeux.go4lunch.utils.liveEvent;

import com.jeremydufeux.go4lunch.models.Restaurant;

public class CreateNotificationLiveEvent implements LiveEvent {
    Restaurant mRestaurant;
    long mTimeBeforeLunch;

    public CreateNotificationLiveEvent(long timeBeforeLunch, Restaurant restaurant) {
        mRestaurant = restaurant;
        mTimeBeforeLunch = timeBeforeLunch;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public long getTimeBeforeLunch() {
        return mTimeBeforeLunch;
    }

    public void setTimeBeforeLunch(long timeBeforeLunch) {
        mTimeBeforeLunch = timeBeforeLunch;
    }
}
