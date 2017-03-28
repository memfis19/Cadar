package io.github.memfis19.cadar.data.process;

import java.util.Date;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.factory.EventFactory;

/**
 * Created by memfis on 3/26/15.
 * Interface that provides general functionality for events generation. For supported event repeat periods.
 *
 * @see io.github.memfis19.cadar.data.entity.property.EventProperties.RepeatPeriod
 */
public interface EventCalculator {

    void setEventFactory(EventFactory eventFactory);

    void setEventsToProcess(List<Event> eventList);

    /**
     * Method provides and generate if needed event list for specified date. Null if no events.
     *
     * @param date - date for which events will be provided.
     * @return - events list for specified date.
     */
    List<Event> getEventsForDate(Date date);

    /**
     * Method provides and generate if needed event list for specified date. Null if no events.
     *
     * @param startDate - start date for generation period
     * @param endDate   - end date for generation period
     * @return - events list for specified date.
     */
    List<Event> getEventsForPeriod(Date startDate, Date endDate);

    /**
     * Method returns dates list for specified events. Null if no events.
     * Can bes used for grouping events by date in case when few events can be at the same day.
     *
     * @param events - events for which we will generate dates.
     * @return - dates list for specified events.
     */
    List<Date> getEventsDates(List<Event> events);

}
