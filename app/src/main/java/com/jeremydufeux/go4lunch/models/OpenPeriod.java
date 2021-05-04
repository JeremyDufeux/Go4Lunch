package com.jeremydufeux.go4lunch.models;

public class OpenPeriod {
    private final int openingDay;
    private final int openingHour;
    private final int openingMinute;
    private final int closingDay;
    private final int closingHour;
    private final int closingMinute;

    public OpenPeriod(int openingDay, int openingHour, int openingMinute, int closingDay, int closingHour, int closingMinute) {
        this.openingDay = openingDay;
        this.openingHour = openingHour;
        this.openingMinute = openingMinute;
        this.closingDay = closingDay;
        this.closingHour = closingHour;
        this.closingMinute = closingMinute;
    }

    public int getOpeningDay() {
        return openingDay;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public int getOpeningMinute() {
        return openingMinute;
    }

    public int getClosingDay() {
        return closingDay;
    }

    public int getClosingHour() {
        return closingHour;
    }

    public int getClosingMinute() {
        return closingMinute;
    }
}
