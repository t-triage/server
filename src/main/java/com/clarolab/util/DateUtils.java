/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import com.google.common.base.Strings;
import lombok.extern.java.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log
public class DateUtils {

    private static final String SEPARATOR = " ";

    private static final String BASE_DATE_FORMAT = "yyyy-MM-dd";
    public static final SimpleDateFormat BaseDateFormat = new SimpleDateFormat(BASE_DATE_FORMAT);

    public static final String TIME_FORMAT_HH_mm_ss = "HH:mm:ss";
    public static final String TIME_FORMAT_HH_mm_ss_S = "HH:mm:ss.S";
    public static final String TIME_FORMAT_HH_mm_ss_SS = "HH:mm:ss.SS";
    public static final String TIME_FORMAT_HH_mm_ss_SSS = "HH:mm:ss.SSS";

    public static final String DATE_FORMAT = "yyyyMMdd" + SEPARATOR + TIME_FORMAT_HH_mm_ss_SSS;
    public static final String BASE_DATE_PATTERN_FORMAT = BASE_DATE_FORMAT + SEPARATOR + TIME_FORMAT_HH_mm_ss_SSS;

    public static final String TIME_PATTERN_HH_mm_ss = "\\d{2}:\\d{2}:\\d{2}";
    public static final String TIME_PATTERN_HH_mm_ss_S = "\\d{2}:\\d{2}:\\d{2}.\\d";
    public static final String TIME_PATTERN_HH_mm_ss_SS = "\\d{2}:\\d{2}:\\d{2}(.\\d{1,2})?";
    public static final String TIME_PATTERN_HH_mm_ss_SSS = "\\d{2}:\\d{2}:\\d{2}(.\\d{1,3})?";

    public static final String DATE_PATTERN = "\\d{8}\\s" + TIME_PATTERN_HH_mm_ss_SSS;

    private static final String DATE_TIME_DELIMITER = "'T'";
    private static final String TIMEZONE_DELIMITER = "Z";

    public static final String TIME_FORMAT_CIRCLE_HH_mm_ss = TIME_FORMAT_HH_mm_ss + "'Z'";
    public static final String TIME_FORMAT_CIRCLE_HH_mm_ss_S = TIME_FORMAT_HH_mm_ss_S + "'Z'";
    public static final String TIME_FORMAT_CIRCLE_HH_mm_ss_SS = TIME_FORMAT_HH_mm_ss_SS + "'Z'";
    public static final String TIME_FORMAT_CIRCLE_HH_mm_ss_SSS = TIME_FORMAT_HH_mm_ss_SSS + "'Z'";

    public static final String DATE_FORMAT_CIRCLE_HH_mm_ss = "yyyy-MM-dd" + DATE_TIME_DELIMITER + TIME_FORMAT_CIRCLE_HH_mm_ss;
    public static final String DATE_FORMAT_CIRCLE_HH_mm_ss_S = "yyyy-MM-dd" + DATE_TIME_DELIMITER + TIME_FORMAT_CIRCLE_HH_mm_ss_SS;
    public static final String DATE_FORMAT_CIRCLE_HH_mm_ss_SS = "yyyy-MM-dd" + DATE_TIME_DELIMITER + TIME_FORMAT_CIRCLE_HH_mm_ss_S;
    public static final String DATE_FORMAT_CIRCLE_HH_mm_ss_SSS = "yyyy-MM-dd" + DATE_TIME_DELIMITER + TIME_FORMAT_CIRCLE_HH_mm_ss_SSS;

    public static final String TIME_PATTERN_CIRCLE_HH_mm_ss = TIME_PATTERN_HH_mm_ss + TIMEZONE_DELIMITER;
    public static final String TIME_PATTERN_CIRCLE_HH_mm_ss_S = TIME_PATTERN_HH_mm_ss_S + TIMEZONE_DELIMITER;
    public static final String TIME_PATTERN_CIRCLE_HH_mm_ss_SS = TIME_PATTERN_HH_mm_ss_SS + TIMEZONE_DELIMITER;
    public static final String TIME_PATTERN_CIRCLE_HH_mm_ss_SSS = TIME_PATTERN_HH_mm_ss_SSS + TIMEZONE_DELIMITER;

    public static final String DATE_PATTERN_CIRCLE_HH_mm_ss = "\\d{4}-\\d{2}-\\d{2}T" + TIME_PATTERN_CIRCLE_HH_mm_ss;
    public static final String DATE_PATTERN_CIRCLE_HH_mm_ss_S = "\\d{4}-\\d{2}-\\d{2}T" + TIME_PATTERN_CIRCLE_HH_mm_ss_S;
    public static final String DATE_PATTERN_CIRCLE_HH_mm_ss_SS = "\\d{4}-\\d{2}-\\d{2}T" + TIME_PATTERN_CIRCLE_HH_mm_ss_SS;
    public static final String DATE_PATTERN_CIRCLE_HH_mm_ss_SSS = "\\d{4}-\\d{2}-\\d{2}T" + TIME_PATTERN_CIRCLE_HH_mm_ss_SSS;

    public static final String DATE_PATTERN_BAMBOO = "yyyy-MM-dd'T'HH:mm:ss.SSSS-HH:mm";

    public static final String DATE_HOUR_SMALL = "MM-dd HH:mm:ss";
    public static final String DATE_SMALL = "MM-dd";

    public static final String TIME_PATTERN_SUREFIRE = "(([0-9]{0,3},)*[0-9]{3}|[0-9]{0,3})*(\\.[0-9]{0,3})?";

    public static Long convertDate(String date) {
        return convertDate(date, null);
    }

    public static Long convertDate(String date, String format) {
        if(Strings.isNullOrEmpty(date))
            return 0L;
        SimpleDateFormat dateFormat;

        if (Strings.isNullOrEmpty(format)) {
            if (date.matches(DATE_PATTERN))
                dateFormat = new SimpleDateFormat(DATE_FORMAT);
            else if (date.matches(DATE_PATTERN_CIRCLE_HH_mm_ss))
                dateFormat = new SimpleDateFormat(DATE_FORMAT_CIRCLE_HH_mm_ss);
            else if (date.matches(DATE_PATTERN_CIRCLE_HH_mm_ss_S))
                dateFormat = new SimpleDateFormat(DATE_FORMAT_CIRCLE_HH_mm_ss_S);
            else if (date.matches(DATE_PATTERN_CIRCLE_HH_mm_ss_SS))
                dateFormat = new SimpleDateFormat(DATE_FORMAT_CIRCLE_HH_mm_ss_SS);
            else if (date.matches(DATE_PATTERN_CIRCLE_HH_mm_ss_SSS))
                dateFormat = new SimpleDateFormat(DATE_FORMAT_CIRCLE_HH_mm_ss_SSS);
            else if (date.matches(DATE_PATTERN_BAMBOO))
                dateFormat = new SimpleDateFormat(DATE_PATTERN_BAMBOO);
            else if (date.matches(TIME_PATTERN_SUREFIRE))
                return getSurfireTime(date);
            else
                throw new RuntimeException("Invalid date.");
        } else {
            dateFormat = new SimpleDateFormat(format);
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(date));
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            log.severe(e.getMessage());
            return 0L;
        }
    }

    public static String covertToString(long timestamp, String format) {
        if (StringUtils.isEmpty(format)) {
            format = DATE_FORMAT_CIRCLE_HH_mm_ss;
        }
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);

        return strDate;
    }

    public static boolean isTime(String time) {
        return time.matches(TIME_PATTERN_HH_mm_ss_SSS);
    }

    public static boolean isDate(String date) {
        return date.matches(DATE_PATTERN);
    }

    private static long getSurfireTime(String time) {
        return Long.parseLong(time.replace(",", ".").replace(".", ""));
    }

    public static long beginDay(int days) {
        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, days);

        return date.getTimeInMillis();
    }


    public static long offSetDays(int days) {
        // today
        Calendar date = new GregorianCalendar();
        // off set
        date.add(Calendar.DAY_OF_YEAR, days);

        return date.getTimeInMillis();
    }

    public static int daysFromToday(long timestamp) {
        return daysFromToday(timestamp, 12);
    }

    public static int daysFromToday(long timestamp, long nextDayFromHsDiff) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId());

        Duration duration = Duration.between(now, deadline);
        int roundDays = 0;
        final long diffHs = duration.toHours();
        if (diffHs < 0 && ((diffHs % 24) < -1)) {
            roundDays = -1;
        }
        if (diffHs > 0 && ((diffHs % 24) > nextDayFromHsDiff)) {
            roundDays = 1;
        }
        int diff = (int) duration.toDays();

        return diff + roundDays;
    }

    public static String tomorrowDeadlineFrequency() {
        String deadlineBase = "0 0 17 * * SUN,MON,TUE,WED,THU,FRI,SAT";
        DateFormat df = new SimpleDateFormat("E");
        String dayOfWeek = df.format(new Date()).toUpperCase();

        if (dayOfWeek.equals("SAT")) {
            deadlineBase = deadlineBase.replace("," + dayOfWeek, "");
        } else {
            deadlineBase = deadlineBase.replace(dayOfWeek + ",", "");
        }

        return deadlineBase;
    }

    public static String getMonthName(long timestamp, boolean shortName) {
        SimpleDateFormat format = null;
        if (shortName) {
            format = new SimpleDateFormat("MMM");
        } else {
            format = new SimpleDateFormat("MMMMM");
        }

        return format.format(new Date(timestamp));
    }

    public static String getDayName(long timestamp, boolean shortName) {
        SimpleDateFormat format = null;
        if (shortName) {
            format = new SimpleDateFormat("EEE");
        } else {
            format = new SimpleDateFormat("EEEE");
        }

        return format.format(new Date(timestamp));
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static Instant instantNow(){
        return Instant.now();
    }

    public static HashMap<Integer, Long> getTimeSlices(){
        HashMap slices = new HashMap();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, -1); //Skip current month
        for(int m = 0; m < 12; m++) {
            slices.put(m, cal.getTimeInMillis());
            cal.add(Calendar.MONTH, -1);
        }
        return slices;
    }

    public static HashMap<Integer, Long> getTimeForDaySlices(){
        HashMap slices = new HashMap();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -1); //Skip current day
        for(int i=0; i < 5; i++) {
            slices.put(i, cal.getTimeInMillis());
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return slices;
    }

    public static HashMap<Integer, Long> getTimeForWeekSlices(){
        HashMap slices = new HashMap();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -7); //Skip current day
        for(int i=0; i < 6; i++) {
            slices.put(i, cal.getTimeInMillis());
            cal.add(Calendar.DAY_OF_YEAR, -7);
        }
        return slices;
    }

    public static String getElapsedTime(Instant start, Instant end){
        long elapsedTime = Duration.between(start, end).toMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)-TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedTime));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String timeAgo(long currentDate, long pastDate) {
        long milliSecPerMinute = 60 * 1000; //Milliseconds Per Minute
        long milliSecPerHour = milliSecPerMinute * 60; //Milliseconds Per Hour
        long milliSecPerDay = milliSecPerHour * 24; //Milliseconds Per Day
        long milliSecPerMonth = milliSecPerDay * 30; //Milliseconds Per Month
        long milliSecPerYear = milliSecPerDay * 365; //Milliseconds Per Year

        //Difference in Milliseconds between two dates
        long msExpired = currentDate - pastDate;

        //Second or Seconds ago calculation
        if (msExpired < milliSecPerMinute) {
            if (Math.round(msExpired / 1000) == 1) {
                return Math.round(msExpired / 1000) + " second ago";
            } else {
                return Math.round(msExpired / 1000) + " seconds ago";
            }
        }
        //Minute or Minutes ago calculation
        else if (msExpired < milliSecPerHour) {
            if (Math.round(msExpired / milliSecPerMinute) == 1) {
                return Math.round(msExpired / milliSecPerMinute) + " minute ago";
            } else {
                return Math.round(msExpired / milliSecPerMinute) + " minutes ago";
            }
        }
        //Hour or Hours ago calculation
        else if (msExpired < milliSecPerDay) {
            if (Math.round(msExpired / milliSecPerHour) == 1) {
                return Math.round(msExpired / milliSecPerHour) + " hour ago";
            } else {
                return Math.round(msExpired / milliSecPerHour) + " hours ago";
            }
        }
        //Day or Days ago calculation
        else if (msExpired < milliSecPerMonth) {
            if (Math.round(msExpired / milliSecPerDay) == 1) {
                return Math.round(msExpired / milliSecPerDay) + " day ago";
            } else {
                return Math.round(msExpired / milliSecPerDay) + " days ago";
            }
        }
        //Month or Months ago calculation
        else if (msExpired < milliSecPerYear) {
            if (Math.round(msExpired / milliSecPerMonth) == 1) {
                return Math.round(msExpired / milliSecPerMonth) + "  month ago";
            } else {
                return Math.round(msExpired / milliSecPerMonth) + "  months ago";
            }
        }
        //Year or Years ago calculation
        else {
            if (Math.round(msExpired / milliSecPerYear) == 1) {
                return Math.round(msExpired / milliSecPerYear) + " year ago";
            } else {
                return Math.round(msExpired / milliSecPerYear) + " years ago";
            }
        }
    }
}
