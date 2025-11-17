package com.example.babydiary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // 서버 날짜 포맷
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SERVER_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    // 표시용 날짜 포맷
    public static final String DISPLAY_DATE_FORMAT = "yyyy년 MM월 dd일";
    public static final String DISPLAY_MONTH_DAY_FORMAT = "MM월 dd일";
    public static final String DISPLAY_TIME_FORMAT = "a h:mm";

    /**
     * Date 객체를 서버 포맷 문자열로 변환
     */
    public static String dateToServerFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 현재 날짜를 서버 포맷 문자열로 반환
     */
    public static String getTodayServerFormat() {
        return dateToServerFormat(new Date());
    }

    /**
     * 서버 날짜 문자열을 표시용 포맷으로 변환
     */
    public static String serverDateToDisplay(String serverDate) {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date date = serverFormat.parse(serverDate);
            return displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return serverDate;
        }
    }

    /**
     * 서버 DateTime 문자열을 표시용 포맷으로 변환
     */
    public static String serverDateTimeToDisplay(String serverDateTime) {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat(SERVER_DATETIME_FORMAT, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT + " " + DISPLAY_TIME_FORMAT, Locale.getDefault());
            Date date = serverFormat.parse(serverDateTime);
            return displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return serverDateTime;
        }
    }

    /**
     * 현재 연도 가져오기
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 현재 주차 번호 가져오기
     */
    public static int getCurrentWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 날짜 문자열을 Date 객체로 변환
     */
    public static Date parseServerDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}