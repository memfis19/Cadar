package io.github.memfis19.cadar.internal.ui.month.adapter.decorator;

import android.view.View;

import io.github.memfis19.cadar.settings.CadarSettings;

/**
 * Created by memfis on 7/15/16.
 */
public interface WeekDayDecorator {
    void onBindWeekDayView(View view, @CadarSettings.DayOfWeeks int dayOfWeek, String title);
}
