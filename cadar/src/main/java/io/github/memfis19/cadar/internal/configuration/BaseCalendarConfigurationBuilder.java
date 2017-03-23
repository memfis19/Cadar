package io.github.memfis19.cadar.internal.configuration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.Calendar;
import java.util.Locale;

import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.data.process.IEventProcessor;
import io.github.memfis19.cadar.data.process.impl.CadarEventProcessor;
import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 7/21/16.
 */
public abstract class BaseCalendarConfigurationBuilder<T> {

    protected Context context;
    protected Locale locale = Locale.getDefault();
    protected Calendar initialDay = DateUtils.getCalendarInstance();

    protected boolean eventProcessingEnabled = false;
    protected IEventProcessor eventProcessor = new CadarEventProcessor();
    protected EventFactory eventFactory;

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

    public BaseCalendarConfigurationBuilder(@NonNull Context context) {
        this.context = context;
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

    public BaseCalendarConfigurationBuilder setEventProcessor(IEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
        return this;
    }

    public BaseCalendarConfigurationBuilder setEventFactory(@NonNull EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public BaseCalendarConfigurationBuilder setDisplayPeriod(int periodType, int periodValue) {
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
