package com.example.babydiary.dialog;

import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * 날짜 선택 다이얼로그 헬퍼
 */
public class DatePickerHelper {

    /**
     * 날짜 선택 리스너
     */
    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int dayOfMonth);
    }

    private Context context;
    private OnDateSelectedListener listener;
    private int year;
    private int month;
    private int dayOfMonth;
    private String title;

    /**
     * 생성자
     * @param context Context
     */
    public DatePickerHelper(Context context) {
        this.context = context;

        // 현재 날짜로 초기화
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 초기 날짜 설정
     * @param year 년
     * @param month 월 (0-11)
     * @param dayOfMonth 일
     * @return DatePickerHelper
     */
    public DatePickerHelper setDate(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        return this;
    }

    /**
     * 제목 설정
     * @param title 제목
     * @return DatePickerHelper
     */
    public DatePickerHelper setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 리스너 설정
     * @param listener OnDateSelectedListener
     * @return DatePickerHelper
     */
    public DatePickerHelper setListener(OnDateSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 다이얼로그 표시
     */
    public void show() {
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(
                context,
                new android.app.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (listener != null) {
                            listener.onDateSelected(year, month, dayOfMonth);
                        }
                    }
                },
                year,
                month,
                dayOfMonth
        );

        if (title != null) {
            dialog.setTitle(title);
        }

        // 최대 날짜를 오늘로 설정 (미래 날짜 선택 방지)
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.show();
    }

    /**
     * 날짜 범위 제한이 있는 다이얼로그 표시
     * @param minDate 최소 날짜 (밀리초)
     * @param maxDate 최대 날짜 (밀리초)
     */
    public void showWithDateRange(long minDate, long maxDate) {
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(
                context,
                new android.app.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (listener != null) {
                            listener.onDateSelected(year, month, dayOfMonth);
                        }
                    }
                },
                year,
                month,
                dayOfMonth
        );

        if (title != null) {
            dialog.setTitle(title);
        }

        // 날짜 범위 설정
        if (minDate > 0) {
            dialog.getDatePicker().setMinDate(minDate);
        }
        if (maxDate > 0) {
            dialog.getDatePicker().setMaxDate(maxDate);
        }

        dialog.show();
    }

    /**
     * 간단한 날짜 선택 다이얼로그 표시
     * @param context Context
     * @param listener 리스너
     */
    public static void showSimple(Context context, OnDateSelectedListener listener) {
        new DatePickerHelper(context)
                .setListener(listener)
                .show();
    }

    /**
     * 제목이 있는 날짜 선택 다이얼로그 표시
     * @param context Context
     * @param title 제목
     * @param listener 리스너
     */
    public static void showWithTitle(Context context, String title, OnDateSelectedListener listener) {
        new DatePickerHelper(context)
                .setTitle(title)
                .setListener(listener)
                .show();
    }
}