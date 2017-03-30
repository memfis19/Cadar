package io.github.memfis19.cadar.internal.configuration;

import android.support.annotation.StringRes;

import java.util.Calendar;
import java.util.Locale;

import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.data.process.EventCalculator;
import io.github.memfis19.cadar.data.process.impl.CadarEventCalculator;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.CadarSettings;

/**
 * Created by memfis on 7/21/16.
 */
public class BaseCalendarConfiguration {

    private Locale locale = Locale.getDefault();
    protected Calendar initialDay = DateUtils.getCalendarInstance();

    @CadarSettings.PeriodType
    protected int periodType = Calendar.YEAR;
    protected int periodValue = 1;

    protected boolean eventProcessingEnabled = false;
    protected EventCalculator eventCalculator = new CadarEventCalculator();
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

    protected BaseCalendarConfiguration() {
    }

    public Locale getLocale() {
        return locale;
    }

    @CadarSettings.PeriodType
    public int getPeriodType() {
        return periodType;
    }

    public int getPeriodValue() {
        return periodValue;
    }

    public Calendar getInitialDay() {
        return initialDay;
    }

    public boolean isEventProcessingEnabled() {
        return eventProcessingEnabled;
    }

    public EventCalculator getEventCalculator() {
        return eventCalculator;
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
}
