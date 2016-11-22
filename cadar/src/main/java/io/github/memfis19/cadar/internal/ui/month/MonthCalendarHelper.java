package io.github.memfis19.cadar.internal.ui.month;

import java.util.Calendar;

import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 7/14/16.
 */
public final class MonthCalendarHelper {

    private final static Calendar today = DateUtils.getCalendarInstance();
    private static Calendar selectedDay = today;

    private MonthCalendarHelper() {

    }

    public static Calendar getToday() {
        return today;
    }

    public static Calendar getSelectedDay() {
        return selectedDay;
    }

    public static void updateSelectedDay(Calendar selectedDay) {
        MonthCalendarHelper.selectedDay = selectedDay;
    }

}
