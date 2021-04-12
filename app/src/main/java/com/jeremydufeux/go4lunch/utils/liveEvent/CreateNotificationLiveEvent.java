package com.jeremydufeux.go4lunch.utils.liveEvent;

import com.jeremydufeux.go4lunch.models.Restaurant;

public class CreateNotificationLiveEvent implements LiveEvent {
    Restaurant mRestaurant;

    public CreateNotificationLiveEvent(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }
}
