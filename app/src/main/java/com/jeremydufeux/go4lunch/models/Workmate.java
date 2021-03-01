package com.jeremydufeux.go4lunch.models;

import java.util.Date;

import javax.annotation.Nullable;

public class Workmate {
    private String uId;
    private String userName;
    @Nullable
    private String email;
    @Nullable
    private String pictureUrl;

    private String chosenRestaurantId;
    private Date lastChosenRestaurantDate;

    public Workmate() {
    }

    public Workmate(String uId, String userName, @Nullable String email, @Nullable String pictureUrl) {
        this.uId = uId;
        this.userName = userName;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(@Nullable String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    public void setChosenRestaurantId(String chosenRestaurantId) {
        this.chosenRestaurantId = chosenRestaurantId;
    }

    public Date getLastChosenRestaurantDate() {
        return lastChosenRestaurantDate;
    }

    public void setLastChosenRestaurantDate(Date lastChosenRestaurantDate) {
        this.lastChosenRestaurantDate = lastChosenRestaurantDate;
    }
}
