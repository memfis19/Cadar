package io.github.memfis19.cadar.internal.configuration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.Calendar;
import java.util.Locale;

import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.data.process.IEventProcessor;
import io.github.memfis19.cadar.data.process.impl.Ical4jEventProcessor;
import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 7/21/16.
 */
public class BaseCalendarConfiguration {

    protected Context context;
    protected Locale locale = Locale.getDefault();
    protected Calendar initialDay = DateUtils.getCalendarInstance();
    protected int maxYearsToDisplay = 10;
    protected int numberOfYearsBeforeCurrent = 1;

    protected boolean eventProcessingEnabled = false;
    protected IEventProcessor eventProcessor = new Ical4jEventProcessor();
    protected EventFactory eventFactory;

    protected boolean weekDayTitleTranslationEnabled = false;
    @StringRes
    protected int
            mondayTitle,
            tuesdayTitle,
            wednesdayTitle,
            thursdayTitle,
            fridayTitle,
            saturdayTitle,
            sundayTitle;

    protected BaseCalendarConfiguration(@NonNull Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Locale getLocale() {
        return locale;
    }

    public Calendar getInitialDay() {
        return initialDay;
    }

    public boolean isEventProcessingEnabled() {
        return eventProcessingEnabled;
    }

    public IEventProcessor getEventProcessor() {
        return eventProcessor;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public boolean isWeekDayTitleTranslationEnabled() {
        return weekDayTitleTranslationEnabled;
    }

    public int getMondayTitle() {
        return mondayTitle;
    }

    public int getTuesdayTitle() {
        return tuesdayTitle;
    }

    public int getWednesdayTitle() {
        return wednesdayTitle;
    }

    public int getThursdayTitle() {
        return thursdayTitle;
    }

    public int getFridayTitle() {
        return fridayTitle;
    }

    public int getSaturdayTitle() {
        return saturdayTitle;
    }

    public int getSundayTitle() {
        return sundayTitle;
    }

    public int getMaxYearsToDisplay() {
        return maxYearsToDisplay;
    }

    public int getNumberOfYearsBeforeCurrent() {
        return numberOfYearsBeforeCurrent;
    }
}
