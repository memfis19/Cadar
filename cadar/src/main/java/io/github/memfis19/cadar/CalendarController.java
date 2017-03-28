package io.github.memfis19.cadar;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.internal.configuration.BaseCalendarConfiguration;


/**
 * Created by memfis on 7/21/16.
 */
public interface CalendarController<T extends BaseCalendarConfiguration, E> {

    void prepareCalendar(T configuration);

    void releaseCalendar();

    void setSelectedDay(Calendar selectedDay, boolean scrollToSelectedDay);

    void displayEvents(List<Event> eventList, @Nullable DisplayEventCallback<E> callback);
}
