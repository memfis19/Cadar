package io.github.memfis19.cadar.internal.ui.month.adapter.decorator;

import android.view.View;

import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;

/**
 * Created by memfis on 7/14/16.
 */
public interface MonthDayDecorator {
    void onBindDayView(View view, Calendar calendar, List<Event> eventList, boolean isSelected, boolean isToday);
}
