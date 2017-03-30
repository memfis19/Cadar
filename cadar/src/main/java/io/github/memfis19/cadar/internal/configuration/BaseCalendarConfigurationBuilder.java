package io.github.memfis19.cadar.internal.configuration;

import android.support.annotation.NonNull;
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
public abstract class BaseCalendarConfigurationBuilder<T> {

    protected Locale locale = Locale.getDefault();
    protected Calendar initialDay = DateUtils.getCalendarInstance();

    protected boolean eventProcessingEnabled = false;

    protected EventCalculator eventCalculator = new CadarEventCalculator();
    protected EventFactory eventFactory;

    @CadarSettings.PeriodType
    protected int periodType = Calendar.YEAR;
    protected int periodValue = 1;


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

    public BaseCalendarConfigurationBuilder() {
    }

    public BaseCalendarConfigurationBuilder setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public BaseCalendarConfigurationBuilder setInitialDay(Calendar initialDay) {
        this.initialDay = initialDay;
        return this;
    }

    public BaseCalendarConfigurationBuilder setEventProcessingEnabled(boolean enabled) {
        this.eventProcessingEnabled = enabled;
        return this;
    }

    public BaseCalendarConfigurationBuilder setEventCalculator(EventCalculator eventCalculator) {
        this.eventCalculator = eventCalculator;
        return this;
    }

    public BaseCalendarConfigurationBuilder setEventFactory(@NonNull EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public BaseCalendarConfigurationBuilder setDisplayPeriod(@CadarSettings.PeriodType int periodType, int periodValue) {
        this.periodType = periodType;
        this.periodValue = periodValue;
        return this;
    }

    public BaseCalendarConfigurationBuilder setWeekTitlesSystemTranslationsEnabled(boolean enabled) {
        this.weekDayTitleTranslationEnabled = enabled;
        return this;
    }

    public BaseCalendarConfigurationBuilder setMondayTitle(@StringRes int mondayTitle) {
        this.mondayTitle = mondayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setTuesdayTitle(@StringRes int tuesdayTitle) {
        this.tuesdayTitle = tuesdayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setWednesdayTitle(@StringRes int wednesdayTitle) {
        this.wednesdayTitle = wednesdayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setThursdayTitle(@StringRes int thursdayTitle) {
        this.thursdayTitle = thursdayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setFridayTitle(@StringRes int fridayTitle) {
        this.fridayTitle = fridayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setSaturdayTitle(@StringRes int saturdayTitle) {
        this.saturdayTitle = saturdayTitle;
        return this;
    }

    public BaseCalendarConfigurationBuilder setSundayTitle(@StringRes int sundayTitle) {
        this.sundayTitle = sundayTitle;
        return this;
    }

    public abstract T build();

}
