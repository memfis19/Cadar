package io.github.memfis19.cadar.data.process.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.data.process.EventCalculator;

/**
 * Cadar
 * Created by memfis on 12/8/16.
 * Copyright © 2016 Applikator.
 */

public class CadarEventCalculator implements EventCalculator {


    public CadarEventCalculator() {

    }

    @Override
    public void setEventFactory(EventFactory eventFactory) {

    }

    @Override
    public void setEventsToProcess(List<Event> eventList) {

    }

    @Override
    public List<Event> getEventsForDate(Date date) {
        return Collections.emptyList();
    }

    @Override
    public List<Event> getEventsForPeriod(Date startDate, Date endDate) {
        return Collections.emptyList();
    }

    @Override
    public List<Date> getEventsDates(List<Event> events) {
        return Collections.emptyList();
    }
}
