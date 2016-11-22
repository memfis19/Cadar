package io.github.memfis19.cadar.internal.utils;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by memfis on 3/26/15.
 * Base collection of useful methods for working with
 *
 * @see java.util.Date
 */
public final class DateUtils {

    private final static String TAG = "DateUtils";

    public static final int MAX_YEAR_RANGE = 50;

    public static final long WEEK_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000;

    public static final String MMMM_yyyy = "MMMM yyyy";
    public static final String cccc_dd = "cccc dd";

    public static final Calendar today = getCalendarInstance();//TODO: need update current value correctly

    public static Locale danish = new Locale("da", "DK");
    public static Map<String, DateFormat> map = new HashMap<>();

    public static Locale getLocale() {
        return danish;
    }

    public static Calendar getCalendarInstance() {
        try {
            return Calendar.getInstance(getTimeZone(), getLocale());
        } catch (Exception e) {
            Log.e(TAG, "Can't create instance of the specified calendar. ");
        }
        return Calendar.getInstance();
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public static Calendar setTimeToYearStart(Calendar calendar) {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }


    public static Calendar setTimeToYearEnd(Calendar calendar) {
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static Date setTimeToYearStart(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        setTimeToMidnight(calendar);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public static Date setTimeToYearEnd(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        setTimeToEndOfTheDay(calendar);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        return calendar.getTime();
    }

    public static Date setTimeToMonthStart(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        setTimeToMidnight(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Calendar setTimeToMonthStart(Calendar calendar) {
        calendar = setTimeToMidnight(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static Date setTimeToMonthEnd(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        setTimeToEndOfTheDay(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Calendar setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * Sets the date time to the midnight.
     *
     * @param date - to set the time
     * @return - the same date with new time
     */
    public static Date setTimeToMidnight(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar = setTimeToMidnight(calendar);

        return calendar.getTime();
    }

    public static Calendar setTimeToEndOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * Sets the date time to the end of the day.
     *
     * @param date - to set the time
     * @return - the same date with new time
     */
    public static Date setTimeToEndOfTheDay(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar = setTimeToEndOfTheDay(calendar);


        return calendar.getTime();
    }

    /**
     * Method returns number of days between two dates using hours/minutes for calculation.
     * Example: start = 20/12/2015T15:00:00, end = 21/12/2015T13:00:00, result = 0 days.
     * Use the same time for different dates to avoid such problem.
     *
     * @param start - start date
     * @param end   - end period
     * @return - number of years between start and end date.
     * @see #setTimeToMidnight(java.util.Date)
     * @see #setTimeToEndOfTheDay(java.util.Date)
     */
    public static int daysBetween(Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        DateTime endDateTime = new DateTime(end);

        return Days.daysBetween(startDateTime, endDateTime).getDays();
    }

    public static int daysBetweenPure(Date start, Date end) {
        DateTime startDateTime = new DateTime(setTimeToMidnight(start));
        DateTime endDateTime = new DateTime(setTimeToMidnight(end));

        return Days.daysBetween(startDateTime, endDateTime).getDays();
    }

    public static int monthBetween(Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        DateTime endDateTime = new DateTime(end);

        return Months.monthsBetween(startDateTime, endDateTime).getMonths();
    }

    public static int monthBetweenPure(Date start, Date end) {
        Calendar startCalendar = getCalendarInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = getCalendarInstance();
        endCalendar.setTime(end);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }


    public static int weeksBetween(Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        DateTime endDateTime = new DateTime(end);

        return Weeks.weeksBetween(startDateTime, endDateTime).getWeeks();
    }

    /**
     * Method returns number of years between two dates using day/month for calculation.
     * Example: start = 20/12/2015, end = 20/11/2016, result = 0 years.
     *
     * @param start - start date
     * @param end   - end period
     * @return - number of years between start and end date.
     */
    public static int yearsBetween(Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        DateTime endDateTime = new DateTime(end);

        return Years.yearsBetween(startDateTime, endDateTime).getYears();
    }

    /**
     * Method returns number of years between two dates using years for calculation.
     * Example: start = 20/12/2015, end = 20/11/2016, result = 1 year.
     *
     * @param start - start date
     * @param end   - end period
     * @return - number of years between start and end date.
     */
    public static int yearsBetweenPure(Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        DateTime endDateTime = new DateTime(end);

        return Math.abs(endDateTime.getYear() - startDateTime.getYear());
    }

    public static void resetTime(GregorianCalendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Return the minimum date for using in application. Now it is 01/01/1900
     *
     * @return - minimum supported date.
     */
    public static Date getMinimumSupportedDate() {
        DateTime minimumDate = new DateTime(1900, 1, 1, 0, 0);
        return minimumDate.toDate();
    }

    /**
     * Return the maximum date for using in application.
     *
     * @return - maximum supported date.
     */
    public static Date getMaximumSupportedDate() {
        DateTime maximumDate = new DateTime(DateTime.now().year().get() + MAX_YEAR_RANGE / 2, 1, 1, 0, 0);
        return maximumDate.toDate();
    }

    public static Date getCurrentDate() {
        DateTime dateTime = new DateTime();
        DateTime currentDate = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 0, 0);
        return currentDate.toDate();
    }

    public static long getCurrentTime() {
        Calendar calendar = getCalendarInstance();
        long offset = calendar.get(Calendar.ZONE_OFFSET);
        return calendar.getTimeInMillis() - offset;
    }

    public static boolean isLastDayOfTheMonth(Date date) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date addYearToDate(Date date, int yearsToAdd) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, yearsToAdd);
        return calendar.getTime();
    }

    public static Date addMonthToDate(Date date, int monthsToAdd) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, monthsToAdd);
        return calendar.getTime();
    }

    public static Date addDayToDate(Date date, int daysToAdd) {
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }

    public static String dateToString(Calendar calendar, String pattern) {
        Date date = calendarToDate(calendar);
        return dateToString(date, pattern);
    }

    public static Date calendarToDate(Calendar calendar) {
        return new Date(calendar.getTimeInMillis());
    }

    public static String dateToString(Date date, String pattern) {
        return getDateFormat(pattern).format(date);
    }

    public static DateFormat getDateFormat(String pattern) {
        if (!map.containsKey(pattern)) {
            DateFormat df = new SimpleDateFormat(pattern, getLocale());
            map.put(pattern, df);
        }
        return map.get(pattern);
    }

    public static boolean isSameDay(Date firstDate, Date secondDate) {
        Calendar firstCalendar = getCalendarInstance();
        Calendar secondCalendar = getCalendarInstance();

        firstCalendar.setTime(firstDate);
        secondCalendar.setTime(secondDate);

        return isSameDay(firstCalendar, secondCalendar);
    }

    public static boolean isSameMonth(Calendar firstCalendar, Calendar secondCalendar) {
        if (firstCalendar == null || secondCalendar == null) return false;
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
                && firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH);
    }

    public static boolean isSameYear(Calendar firstCalendar, Calendar secondCalendar) {
        if (firstCalendar == null || secondCalendar == null) return false;
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR);
    }

    public static boolean isSameDay(Calendar firstDate, Calendar secondDate) {
        return (firstDate.get(Calendar.DAY_OF_YEAR) == secondDate.get(Calendar.DAY_OF_YEAR)
                && firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR));
    }

    //TODO: find other solution without creation of Calendar instance for performance

    public static Date roundDateToMinute(Date date) {
        if (date == null) return null;
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.SECOND) > 30) {
            calendar.add(Calendar.MINUTE, 1);
        }

        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public static Date cutDateSeconds(Date date) {
        if (date == null) return null;
        Calendar calendar = getCalendarInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * <p>Checks if a date is today.</p>
     *
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, today.getTime());
    }

    /**
     * <p>Checks if a calendar date is today.</p>
     *
     * @param calendar the calendar, not altered, not null
     * @return true if cal date is today
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar calendar) {
        return isSameDay(calendar, today);
    }
}
