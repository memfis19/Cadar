package io.github.memfis19.cadar.settings;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.util.Calendar;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfigurationBuilder;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.WeekDayDecorator;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;

/**
 * Created by memfis on 7/14/16.
 */
public final class MonthCalendarConfiguration extends BaseCalendarConfiguration {

    @CadarSettings.DayOfWeeks
    private int firstDayOfWeek = Calendar.MONDAY;

    private boolean displayDaysOutOfMonth = true;

    private MonthDayDecoratorFactory monthDayDecoratorFactory;
    private WeekDayDecorator weekDayDecorator;
    @LayoutRes
    private int
            monthLayoutId = R.layout.month_calendar_event_layout,
            weekTitleLayoutId = R.layout.month_calendar_day_of_week_layout;


    private MonthCalendarConfiguration(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseCalendarConfigurationBuilder<MonthCalendarConfiguration> {

        private MonthCalendarConfiguration monthCalendarConfiguration;

        public Builder(@NonNull Context context) {
            super(context);
            monthCalendarConfiguration = new MonthCalendarConfiguration(context);
        }

        public Builder setFirstDayOfWeek(@CadarSettings.DayOfWeeks int firstDayOfWeek) {
            monthCalendarConfiguration.firstDayOfWeek = firstDayOfWeek;
            return this;
        }

        public Builder setMonthDayLayout(@LayoutRes int monthLayoutId, MonthDayDecoratorFactory monthDayDecoratorFactory) {
            monthCalendarConfiguration.monthLayoutId = monthLayoutId;
            monthCalendarConfiguration.monthDayDecoratorFactory = monthDayDecoratorFactory;
            return this;
        }

        public Builder setDayWeekTitleLayout(@LayoutRes int weekTitleLayoutId, WeekDayDecorator weekDayDecorator) {
            monthCalendarConfiguration.weekTitleLayoutId = weekTitleLayoutId;
            monthCalendarConfiguration.weekDayDecorator = weekDayDecorator;
            return this;
        }

        public Builder setDisplayDaysOutOfMonth(boolean displayDaysOutOfMonth) {
            monthCalendarConfiguration.displayDaysOutOfMonth = displayDaysOutOfMonth;
            return this;
        }

        @Override
        public MonthCalendarConfiguration build() throws NullPointerException, IllegalStateException, IllegalArgumentException {
            if (context == null)
                throw new NullPointerException("Passed activity to month calendar configuration can't be null.");

            if (initialDay == null)
                throw new NullPointerException("Passed initial day can't be null.");
            monthCalendarConfiguration.initialDay = initialDay;

            if (eventProcessingEnabled && eventProcessor == null)
                throw new IllegalStateException("Configuration set to process events. But event processor not passed or null.");
            monthCalendarConfiguration.eventProcessingEnabled = eventProcessingEnabled;
            monthCalendarConfiguration.eventProcessor = eventProcessor;

            if (eventProcessingEnabled && eventFactory == null)
                throw new NullPointerException("Event factory is null. Please setup it.");
            monthCalendarConfiguration.eventFactory = eventFactory;
            monthCalendarConfiguration.eventProcessor.setEventFactory(monthCalendarConfiguration.eventFactory);

            if (weekDayTitleTranslationEnabled) {
                if (mondayTitle == 0
                        || tuesdayTitle == 0
                        || wednesdayTitle == 0
                        || thursdayTitle == 0
                        || fridayTitle == 0
                        || saturdayTitle == 0
                        || sundayTitle == 0) {
                    throw new IllegalArgumentException("Configuration set to override default week day titles, but not all titles are passed.");
                }
            }
            monthCalendarConfiguration.weekDayTitleTranslationEnabled = weekDayTitleTranslationEnabled;
            if (monthCalendarConfiguration.weekDayTitleTranslationEnabled) {
                monthCalendarConfiguration.mondayTitle = mondayTitle;
                monthCalendarConfiguration.tuesdayTitle = tuesdayTitle;
                monthCalendarConfiguration.wednesdayTitle = wednesdayTitle;
                monthCalendarConfiguration.thursdayTitle = thursdayTitle;
                monthCalendarConfiguration.fridayTitle = fridayTitle;
                monthCalendarConfiguration.saturdayTitle = saturdayTitle;
                monthCalendarConfiguration.sundayTitle = sundayTitle;
            }

            return monthCalendarConfiguration;
        }
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public int getMonthLayoutId() {
        return monthLayoutId;
    }

    public MonthDayDecoratorFactory getMonthDayDecoratorFactory() {
        return monthDayDecoratorFactory;
    }

    public int getWeekTitleLayoutId() {
        return weekTitleLayoutId;
    }

    public WeekDayDecorator getWeekDayDecorator() {
        return weekDayDecorator;
    }

    public boolean isDisplayDaysOutOfMonth() {
        return displayDaysOutOfMonth;
    }
}
