package com.jeremydufeux.go4lunch.models.PlaceDetailsResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jeremydufeux.go4lunch.models.PlaceResult.Location;
import com.jeremydufeux.go4lunch.models.PlaceResult.Viewport;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}