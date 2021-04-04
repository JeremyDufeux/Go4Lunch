package com.jeremydufeux.go4lunch.utils;

import com.google.firebase.Timestamp;

import java.util.Calendar;

public class Utils {

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static Timestamp getTodayStartTimestamp(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.clear(Calendar.MINUTE);
        now.clear(Calendar.SECOND);
        return new Timestamp(now.getTime());
    }

    public static Timestamp getTodayEndTimestamp(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.clear(Calendar.MINUTE);
        now.clear(Calendar.SECOND);
        now.add(Calendar.DAY_OF_MONTH, 1);
        return new Timestamp(now.getTime());
    }
}
