package com.example.babydiary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 날짜 관련 유틸리티
 */
public class DateUtils {

    // 날짜 포맷
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN);
    private static final SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN);

    /**
     * 오늘 날짜를 "yyyy-MM-dd" 형식으로 반환
     */
    public static String getTodayString() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * Date를 "yyyy-MM-dd" 형식 문자열로 변환
     */
    public static String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * "yyyy-MM-dd" 형식 문자열을 Date로 변환
     */
    public static Date stringToDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * "yyyy-MM-dd" 형식을 표시용 형식으로 변환
     * 예: "2025-01-06" -> "2025년 01월 06일"
     */
    public static String formatForDisplay(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * ISO 8601 datetime을 표시용 형식으로 변환
     * 예: "2025-01-06T10:30:00" -> "2025년 01월 06일 10:30"
     */
    public static String formatDateTimeForDisplay(String datetimeString) {
        try {
            // ISO 8601 형식 파싱 (T 포함)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(datetimeString);
            return DISPLAY_TIME_FORMAT.format(date);
        } catch (ParseException e) {
            // 실패 시 기본 datetime 형식 시도
            try {
                Date date = DATETIME_FORMAT.parse(datetimeString);
                return DISPLAY_TIME_FORMAT.format(date);
            } catch (ParseException ex) {
                return datetimeString;
            }
        }
    }

    /**
     * 날짜의 년도 반환
     */
    public static int getYear(String dateString) {
        Date date = stringToDate(dateString);
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        }
        return 0;
    }

    /**
     * 날짜의 주차 반환 (ISO 8601 기준)
     */
    public static int getWeekNumber(String dateString) {
        Date date = stringToDate(dateString);
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setMinimalDaysInFirstWeek(4);
            return calendar.get(Calendar.WEEK_OF_YEAR);
        }
        return 0;
    }

    /**
     * 두 날짜 사이의 일수 차이 계산
     */
    public static long getDaysBetween(String startDateString, String endDateString) {
        Date startDate = stringToDate(startDateString);
        Date endDate = stringToDate(endDateString);

        if (startDate != null && endDate != null) {
            long diff = endDate.getTime() - startDate.getTime();
            return diff / (1000 * 60 * 60 * 24);
        }
        return 0;
    }

    /**
     * 오늘로부터 며칠 전/후인지 계산
     * @param dateString 날짜
     * @return 양수면 미래, 음수면 과거, 0이면 오늘
     */
    public static long getDaysFromToday(String dateString) {
        return getDaysBetween(getTodayString(), dateString);
    }
}
