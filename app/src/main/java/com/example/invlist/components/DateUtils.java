package com.example.invlist.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static int getDaysCount(String curDate, String curFormat, String oldDate, String oldFormat) {
        try {
            Date curDateObj = new SimpleDateFormat(curFormat).parse(curDate);
            Date oldDateObj = new SimpleDateFormat(oldFormat).parse(oldDate);

            long days = curDateObj.getTime() - oldDateObj.getTime();
            return (int) TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            return 0;
        }
    }

    public static boolean isSameDate(String curDate, String curFormat, String oldDate, String oldFormat) {
        try {
            Date curDateObj = new SimpleDateFormat(curFormat).parse(curDate);
            Date oldDateObj = new SimpleDateFormat(oldFormat).parse(oldDate);

            return curDateObj.getDate() == oldDateObj.getDate();
        } catch (ParseException e) {
            return false;
        }
    }
}
