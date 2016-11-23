package io.github.memfis19.sample.model;

import java.util.Date;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.entity.property.EventProperties;

/**
 * Created by memfis on 11/23/16.
 */

public class EventModel implements Event {

    private long id = System.currentTimeMillis();
    private String title = "Event #" + String.valueOf(id);
    private String description = "";
    private Date originalStartDate = new Date();
    private Date startDate = (Date) originalStartDate.clone();
    private Date endDate = new Date();

    public EventModel() {

    }

    public EventModel(Event event) {
        this.id = event.getEventId();
        this.title = event.getEventTitle();
        this.description = event.getEventDescription();
        this.originalStartDate = event.getOriginalEventStartDate();
        this.startDate = event.getEventStartDate();
        this.endDate = event.getEventEndDate();
    }

    @Override
    public Long getEventId() {
        return id;
    }

    @Override
    public String getEventTitle() {
        return title;
    }

    @Override
    public String getEventDescription() {
        return description;
    }

    @Override
    public Date getOriginalEventStartDate() {
        return startDate;
    }

    @Override
    public Date getEventStartDate() {
        return startDate;
    }

    @Override
    public Date getEventEndDate() {
        return endDate;
    }

    @Override
    public void setEventStartDate(Date startEventDate) {
        this.startDate = startEventDate;
    }

    @Override
    public void setEventEndDate(Date endEventDate) {
        this.endDate = endEventDate;
    }

    @Override
    public void setCalendarId(Long calendarId) {

    }

    @Override
    public Long getCalendarId() {
        return 0l;
    }

    @Override
    public int getEventRepeatPeriod() {
        return EventProperties.EVERY_WEEK;
    }

    @Override
    public String getEventIconUrl() {
        return "";
    }

    @Override
    public Boolean isEditable() {
        return false;
    }

    @Override
    public Boolean isAllDayEvent() {
        return false;
    }
}
