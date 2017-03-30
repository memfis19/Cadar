package io.github.memfis19.cadar.settings;

import android.support.annotation.LayoutRes;
import android.util.SparseArray;

import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfigurationBuilder;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
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
    private EventsProcessor<Calendar, SparseArray<List<Event>>> eventsProcessor;


    @LayoutRes
    private int
            monthLayoutId = R.layout.month_calendar_event_layout,
            weekTitleLayoutId = R.layout.month_calendar_day_of_week_layout;


    private MonthCalendarConfiguration() {
        super();
    }

    public static class Builder extends BaseCalendarConfigurationBuilder<MonthCalendarConfiguration> {

        private MonthCalendarConfiguration monthCalendarConfiguration;

        public Builder() {
            super();
            monthCalendarConfiguration = new MonthCalendarConfiguration();
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

        public Builder setEventsProcessor(EventsProcessor<Calendar, SparseArray<List<Event>>> eventsProcessor) {
            monthCalendarConfiguration.eventsProcessor = eventsProcessor;
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
            if (initialDay == null)
                throw new NullPointerException("Passed initial day can't be null.");
            monthCalendarConfiguration.initialDay = initialDay;

            if (eventProcessingEnabled && eventCalculator == null)
                throw new IllegalStateException("Configuration set to process events. But event processor not passed or null.");
            monthCalendarConfiguration.eventProcessingEnabled = eventProcessingEnabled;
            monthCalendarConfiguration.eventCalculator = eventCalculator;

            if (eventProcessingEnabled && eventFactory == null)
                throw new NullPointerException("Event factory is null. Please setup it.");
            monthCalendarConfiguration.eventFactory = eventFactory;
            monthCalendarConfiguration.eventCalculator.setEventFactory(monthCalendarConfiguration.eventFactory);

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

            if (periodType != Calendar.MONTH && periodType != Calendar.YEAR)
                throw new IllegalArgumentException("Period type should be Calendar.MONTH or Calendar.YEAR only.");
            if (periodValue < 1)
                throw new IllegalArgumentException("Period value should be more then 1.");
            if (periodType == Calendar.MONTH && periodValue < 3)
                throw new IllegalStateException("In case with Calendar.MONTH period type, minimum value should be GE 3.");

            monthCalendarConfiguration.periodType = periodType;
            monthCalendarConfiguration.periodValue = periodValue;

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

    public EventsProcessor<Calendar, SparseArray<List<Event>>> getEventsProcessor() {
        return eventsProcessor;
    }
}
