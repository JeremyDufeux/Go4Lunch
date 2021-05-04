package com.jeremydufeux.go4lunch.models;

public class OpenPeriod {
    private final int mOpeningDay;
    private final int mOpeningHour;
    private final int mOpeningMinute;
    private final int mClosingDay;
    private final int mClosingHour;
    private final int mClosingMinute;

    public OpenPeriod(int openingDay,
                      int openingHour,
                      int openingMinute,
                      int closingDay,
                      int closingHour,
                      int closingMinute) {
        mOpeningDay = openingDay;
        mOpeningHour = openingHour;
        mOpeningMinute = openingMinute;
        mClosingDay = closingDay;
        mClosingHour = closingHour;
        mClosingMinute = closingMinute;
    }

    public int getOpeningDay() {
        return mOpeningDay;
    }

    public int getOpeningHour() {
        return mOpeningHour;
    }

    public int getOpeningMinute() {
        return mOpeningMinute;
    }

    public int getClosingDay() {
        return mClosingDay;
    }

    public int getClosingHour() {
        return mClosingHour;
    }

    public int getClosingMinute() {
        return mClosingMinute;
    }
}
