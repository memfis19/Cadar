package io.github.memfis19.cadar.settings;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfigurationBuilder;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.EventDecoratorFactory;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.MonthDecoratorFactory;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.WeekDecoratorFactory;

/**
 * Created by serg on 13.09.16.
 */
public class ListCalendarConfiguration extends BaseCalendarConfiguration {

    private int capacityYears = 3;

    private EventDecoratorFactory eventDecoratorFactory;
    private WeekDecoratorFactory weekDecoratorFactory;
    private MonthDecoratorFactory monthDecoratorFactory;

    @LayoutRes
    private int
            eventLayoutId = R.layout.event_layout,
            weekLayoutId = R.layout.week_title_layout,
            monthLayoutId = R.layout.list_calendar_item_layout;

    private ListCalendarConfiguration(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseCalendarConfigurationBuilder<ListCalendarConfiguration> {

        private ListCalendarConfiguration listCalendarConfiguration;

        public Builder(@NonNull Context context) {
            super(context);
            listCalendarConfiguration = new ListCalendarConfiguration(context);
        }

        public Builder setYearsCapacity(int yearsCapacity) {
            listCalendarConfiguration.capacityYears = yearsCapacity;
            return this;
        }

        public Builder setEventLayout(@LayoutRes int layoutId, EventDecoratorFactory eventDecoratorFactory) {
            listCalendarConfiguration.eventLayoutId = layoutId;
            listCalendarConfiguration.eventDecoratorFactory = eventDecoratorFactory;
            return this;
        }

        public Builder setWeekLayout(@LayoutRes int layoutId, WeekDecoratorFactory weekDecoratorFactory) {
            listCalendarConfiguration.weekLayoutId = layoutId;
            listCalendarConfiguration.weekDecoratorFactory = weekDecoratorFactory;
            return this;
        }

        public Builder setMonthLayout(@LayoutRes int layoutId, MonthDecoratorFactory monthDecoratorFactory) {
            listCalendarConfiguration.monthLayoutId = layoutId;
            listCalendarConfiguration.monthDecoratorFactory = monthDecoratorFactory;
            return this;
        }

        @Override
        public ListCalendarConfiguration build() {
            if (context == null)
                throw new NullPointerException("Passed activity to month calendar configuration can't be null.");

            if (eventProcessingEnabled && eventProcessor == null)
                throw new IllegalStateException("Configuration set to process events. But event processor not passed or null.");
            listCalendarConfiguration.eventProcessingEnabled = eventProcessingEnabled;
            listCalendarConfiguration.eventProcessor = eventProcessor;

            if (eventProcessingEnabled && eventFactory == null)
                throw new NullPointerException("Event factory is null. Please setup it.");
            listCalendarConfiguration.eventFactory = eventFactory;
            listCalendarConfiguration.eventProcessor.setEventFactory(listCalendarConfiguration.eventFactory);

            return listCalendarConfiguration;
        }
    }

    public int getCapacityYears() {
        return capacityYears;
    }

    public EventDecoratorFactory getEventDecoratorFactory() {
        return eventDecoratorFactory;
    }

    public WeekDecoratorFactory getWeekDecoratorFactory() {
        return weekDecoratorFactory;
    }

    public MonthDecoratorFactory getMonthDecoratorFactory() {
        return monthDecoratorFactory;
    }

    public int getEventLayoutId() {
        return eventLayoutId;
    }

    public int getWeekLayoutId() {
        return weekLayoutId;
    }

    public int getMonthLayoutId() {
        return monthLayoutId;
    }
}
