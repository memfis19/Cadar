package io.github.memfis19.cadar.data.process.impl;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;

import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.entity.property.EventProperties;
import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.data.process.IEventProcessor;
import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 3/26/15.
 * Base event processing implementation of
 *
 * @see IEventProcessor
 */
public class Ical4jEventProcessor implements IEventProcessor {

    private final static String TAG = "Ical4jEventProcessor";

    private List<Event> eventsToProcess;
    private EventFactory eventFactory;
    private ComponentFactory componentFactory;

    public Ical4jEventProcessor() {
        this.componentFactory = ComponentFactory.getInstance();
    }

    public Ical4jEventProcessor(List<Event> eventsToProcess) {
        this.eventsToProcess = eventsToProcess;
        this.componentFactory = ComponentFactory.getInstance();
    }

    @Override
    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public void setEventsToProcess(List<Event> eventsToProcess) {
        this.eventsToProcess = eventsToProcess;
    }

    @Override
    public List<Event> getEventsForDate(Date date) {
        return getEventsForPeriod(date, null);
    }

    @Override
    public List<Event> getEventsForPeriod(Date startDate, Date endDate) {
        if (eventsToProcess == null || eventsToProcess.isEmpty() || startDate == null)
            return new ArrayList<>();

        List<Event> eventsToProcess = new ArrayList<>(this.eventsToProcess);
        List<Event> resultEventsList = new ArrayList<>();


        Date specifiedStartDate = DateUtils.setTimeToMidnight(startDate);
        Date specifiedEndDate = endDate == null ? DateUtils.setTimeToEndOfTheDay(startDate) : DateUtils.setTimeToEndOfTheDay(endDate);

        DateTime startDateTime = new DateTime(specifiedStartDate);
        DateTime endDateTime = new DateTime(specifiedEndDate);
        Period period = new Period(startDateTime, endDateTime);

        for (Event event : eventsToProcess) {

            // Skip event without start date
            if (event.getEventStartDate() == null) continue;

            Date eventStartDate = DateUtils.setTimeToMidnight(event.getEventStartDate());
            // Skip any event, with start date greater then end of specified period date.
            if (specifiedEndDate.getTime() < eventStartDate.getTime())
                continue;

            // Skip any event, with end date less then start of specified period date.
            if (event.getEventEndDate() != null
                    && (specifiedStartDate.getTime() > event.getEventEndDate().getTime()))
                continue;

            // Skip not repeatable event, with empty end date and start date less then start of
            // specified period date.
            if (EventProperties.NONE == event.getEventRepeatPeriod() && event.getEventEndDate() == null
                    && (specifiedStartDate.getTime() > eventStartDate.getTime()))
                continue;

            if (EventProperties.NONE == event.getEventRepeatPeriod() && event.getEventEndDate() == null) {
                event.setEventEndDate(DateUtils.setTimeToEndOfTheDay(event.getEventStartDate()));
            }

            EventComponentCreator eventComponentCreator = new EventComponentCreator(event);
            Component component = eventComponentCreator.createEventComponent(componentFactory);
            PeriodList periodList = component.calculateRecurrenceSet(period);

            resultEventsList.addAll(getEventsForPeriodList(event, periodList));
        }
        return resultEventsList;
    }

    @Override
    public List<Date> getEventsDates(List<Event> events) {
        if (events == null) return null;

        Set<Date> dateSet = new HashSet<>();
        for (Event event : events) {
            dateSet.add(DateUtils.setTimeToMidnight(event.getEventStartDate()));
        }

        return new ArrayList<>(dateSet);
    }

    private List<Event> getEventsForPeriodList(Event recurrentEvent, PeriodList periodListForRecurrentEvent) {
        if (periodListForRecurrentEvent == null) return null;

        List<Event> recurrentEventList = new ArrayList<>(periodListForRecurrentEvent.size());

        for (Period period : (Set<Period>) periodListForRecurrentEvent) {

            org.joda.time.DateTime dateTime = new org.joda.time.DateTime(new Date(period.getStart().getTime()), DateTimeZone.UTC);

            Event event = eventFactory.createEventCopy(recurrentEvent);

            final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
            long time = event.getEventStartDate().getTime() % MILLIS_PER_DAY;
            event.setEventStartDate(new Date(dateTime.toDate().getTime() + time));
            recurrentEventList.add(event);
        }

        return recurrentEventList;
    }

}
