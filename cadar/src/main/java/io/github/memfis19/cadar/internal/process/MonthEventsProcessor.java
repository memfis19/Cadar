package io.github.memfis19.cadar.internal.process;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.process.EventCalculator;
import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 7/22/16.
 */
public class MonthEventsProcessor extends EventsProcessor<Calendar, SparseArray<List<Event>>> {

    public MonthEventsProcessor(boolean shouldProcess, EventCalculator eventProcessor) {
        super(shouldProcess, eventProcessor, false);
    }

    @Override
    protected SparseArray<List<Event>> processEvents(Calendar target) {

        List<Event> result;

        final SparseArray<List<Event>> calendarEvents = new SparseArray<>();

        if (isShouldProcess()) {
            Date start = DateUtils.setTimeToMonthStart(target.getTime());
            Date end = DateUtils.setTimeToMonthEnd(target.getTime());

            result = getEventProcessor().getEventsForPeriod(start, end);
        } else
            result = new ArrayList<>(getEventList() == null ? new ArrayList<Event>() : getEventList());

        Calendar temp = DateUtils.getCalendarInstance();
        for (Event event : result) {
            temp.setTime(event.getEventStartDate());

            if (!DateUtils.isSameMonth(temp, target)) continue;

            List<Event> events = calendarEvents.get(temp.get(Calendar.DAY_OF_MONTH), new ArrayList<Event>());
            events.add(event);

            calendarEvents.put(temp.get(Calendar.DAY_OF_MONTH), events);
        }
        result.clear();

        return calendarEvents;
    }
}
