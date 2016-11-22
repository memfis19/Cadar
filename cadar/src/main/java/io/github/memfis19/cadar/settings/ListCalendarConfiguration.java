package io.github.memfis19.cadar.settings;

import android.content.Context;
import android.support.annotation.NonNull;

import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfigurationBuilder;

/**
 * Created by serg on 13.09.16.
 */
public class ListCalendarConfiguration extends BaseCalendarConfiguration {

    private int capacityYears = 3;

    protected ListCalendarConfiguration(@NonNull Context context) {
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

        @Override
        public ListCalendarConfiguration build() {
            return listCalendarConfiguration;
        }
    }

    public int getCapacityYears() {
        return capacityYears;
    }
}
