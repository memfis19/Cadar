package io.github.memfis19.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.entity.property.EventProperties;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.ExtendedMonthCalendar;
import io.github.memfis19.cadar.view.ListCalendar;
import io.github.memfis19.cadar.view.MonthCalendar;

public class MainActivity extends AppCompatActivity implements CalendarPrepareCallback {

    private ExtendedMonthCalendar extendedMonthCalendar;
    private MonthCalendar monthCalendar;
    private ListCalendar listCalendar;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        events.add(new EventModel());

        extendedMonthCalendar = (ExtendedMonthCalendar) findViewById(R.id.extended_calendar);
        monthCalendar = (MonthCalendar) findViewById(R.id.monthCalendar);
        listCalendar = (ListCalendar) findViewById(R.id.listCalendar);

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
        ListCalendarConfiguration.Builder listBuilder = new ListCalendarConfiguration.Builder(this);

        extendedMonthCalendar.setup(builder.build(), Calendar.getInstance(), Calendar.MONDAY, events, getResources().getColor(R.color.material_light_background_color));
        monthCalendar.setCalendarPrepareCallback(this);
        listCalendar.setCalendarPrepareCallback(this);

        monthCalendar.prepareCalendar(builder.build());
        listCalendar.prepareCalendar(listBuilder.build());

        monthCalendar.setOnDayChangeListener(new OnDayChangeListener() {
            @Override
            public void onDayChanged(Calendar calendar) {
                listCalendar.setSelectedDay(DateUtils.setTimeToMidnight((Calendar) calendar.clone()), false);
            }
        });
        monthCalendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChanged(Calendar calendar) {
                listCalendar.setSelectedDay(DateUtils.setTimeToMonthStart((Calendar) calendar.clone()), false);
            }
        });

        listCalendar.setOnDayChangeListener(new OnDayChangeListener() {
            @Override
            public void onDayChanged(Calendar calendar) {
                monthCalendar.setSelectedDay(calendar, false);
            }
        });

        listCalendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChanged(Calendar calendar) {
                monthCalendar.setSelectedDay(calendar, true);
            }
        });
    }

    @Override
    public void onCalendarReady(CalendarController calendar) {
        if (calendar == monthCalendar) {
            monthCalendar.displayEvents(events, new DisplayEventCallback() {
                @Override
                public void onEventsDisplayed() {

                }
            });
        } else if (calendar == listCalendar) {
            listCalendar.displayEvents(events, new DisplayEventCallback() {
                @Override
                public void onEventsDisplayed() {

                }
            });
        }
    }

    class EventModel implements Event {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        monthCalendar.releaseCalendar();
        listCalendar.releaseCalendar();
    }
}
