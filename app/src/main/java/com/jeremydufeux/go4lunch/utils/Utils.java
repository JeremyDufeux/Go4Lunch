package com.jeremydufeux.go4lunch.utils;

import java.util.Calendar;

public class Utils {

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
