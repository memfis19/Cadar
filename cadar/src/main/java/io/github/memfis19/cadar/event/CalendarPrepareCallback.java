package io.github.memfis19.cadar.event;

import io.github.memfis19.cadar.CalendarController;

/**
 * Created by serg on 05.10.16.
 */

public interface CalendarPrepareCallback {
    void onCalendarReady(CalendarController calendar);
}
