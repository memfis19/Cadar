package io.github.memfis19.cadar.settings;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfigurationBuilder;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.EventDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.MonthDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.WeekDecorator;

/**
 * Created by serg on 13.09.16.
 */
public class ListCalendarConfiguration extends BaseCalendarConfiguration {

    private int capacityYears = 3;

    private EventDecorator eventDecorator;
    private WeekDecorator weekDecorator;
    private MonthDecorator monthDecorator;

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

        public Builder setEventLayout(@LayoutRes int layoutId, EventDecorator eventDecorator) {
            listCalendarConfiguration.eventLayoutId = layoutId;
            listCalendarConfiguration.eventDecorator = eventDecorator;
            return this;
        }

        public Builder setWeekLayout(@LayoutRes int layoutId, WeekDecorator weekDecorator) {
            listCalendarConfiguration.weekLayoutId = layoutId;
            listCalendarConfiguration.weekDecorator = weekDecorator;
            return this;
        }

        public Builder setMonthLayout(@LayoutRes int layoutId, MonthDecorator monthDecorator) {
            listCalendarConfiguration.monthLayoutId = layoutId;
            listCalendarConfiguration.monthDecorator = monthDecorator;
            return this;
        }

        @Override
        public ListCalendarConfiguration build() {
            return listCalendarConfiguration;
        }
    }

    public int getCapacityYears() {
        return capacityYears;
    }

    public EventDecorator getEventDecorator() {
        return eventDecorator;
    }

    public WeekDecorator getWeekDecorator() {
        return weekDecorator;
    }

    public MonthDecorator getMonthDecorator() {
        return monthDecorator;
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
