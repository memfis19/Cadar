package io.github.memfis19.cadar.internal.process;

import android.support.v4.util.Pair;

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
public class ListEventsProcessor extends EventsProcessor<Pair<Calendar, Calendar>, List<Event>> {

    public ListEventsProcessor(boolean shouldProcess, EventCalculator eventProcessor) {
        super(shouldProcess, eventProcessor, false);
    }

    @Override
    protected List<Event> processEvents(Pair<Calendar, Calendar> listQueueItem) {
        List<Event> result;

        Calendar startCalendar;
        Calendar endCalendar;

        if (listQueueItem.first != null && listQueueItem.second != null) {
            startCalendar = listQueueItem.first;
            endCalendar = listQueueItem.second;
        } else if (listQueueItem.first == null && listQueueItem.second != null) {
            startCalendar = listQueueItem.second;
            endCalendar = listQueueItem.second;
        } else if (listQueueItem.first != null && listQueueItem.second == null) {
            startCalendar = listQueueItem.first;
            endCalendar = listQueueItem.first;
        } else return new ArrayList<>();

        if (startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()) {
            Calendar temp = startCalendar;
            startCalendar = endCalendar;
            endCalendar = temp;
        }

        if (isShouldProcess()) {
            Date start = DateUtils.setTimeToMonthStart(startCalendar.getTime());
            Date end = DateUtils.setTimeToMonthEnd(endCalendar.getTime());

            result = getEventProcessor().getEventsForPeriod(start, end);
        } else result = new ArrayList<>(getEventList());

        return result;
    }
}
