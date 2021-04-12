package com.jeremydufeux.go4lunch.utils;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static boolean isToday(Date date) {
        Calendar now = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return (cal.get(Calendar.ERA) == now.get(Calendar.ERA) &&
                cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR));
    }

    public static Timestamp getTodayStartTimestamp(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.clear(Calendar.MINUTE);
        now.clear(Calendar.SECOND);
        now.add(Calendar.SECOND, -1);
        return new Timestamp(now.getTime());
    }

    public static Timestamp getTodayEndTimestamp(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.clear(Calendar.MINUTE);
        now.clear(Calendar.SECOND);
        now.add(Calendar.DAY_OF_MONTH, 1);
        now.add(Calendar.SECOND, -1);
        return new Timestamp(now.getTime());
    }

    public static long getMillisToLunchTime(){
        Calendar lunchTime = Calendar.getInstance();
        lunchTime.set(Calendar.HOUR_OF_DAY, 12);
        lunchTime.set(Calendar.MINUTE, 0);
        lunchTime.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();

        return lunchTime.getTimeInMillis() - now.getTimeInMillis();
    }
}
