package io.github.memfis19.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.ListCalendar;
import io.github.memfis19.cadar.view.MonthCalendar;
import io.github.memfis19.sample.model.EventModel;

/**
 * Created by memfis on 11/23/16.
 */

public class MonthListCalendarInteractionActivity extends AppCompatActivity implements CalendarPrepareCallback {

    private MonthCalendar monthCalendar;
    private ListCalendar listCalendar;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_list_interaction_layout);

        events.add(new EventModel());

        monthCalendar = (MonthCalendar) findViewById(R.id.monthCalendar);
        listCalendar = (ListCalendar) findViewById(R.id.listCalendar);

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
        ListCalendarConfiguration.Builder listBuilder = new ListCalendarConfiguration.Builder(this);

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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        monthCalendar.releaseCalendar();
        listCalendar.releaseCalendar();
    }

}
