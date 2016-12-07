package io.github.memfis19.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.MonthCalendar;
import io.github.memfis19.sample.model.EventModel;

/**
 * Created by memfis on 11/23/16.
 */

public class MonthCalendarActivity extends AppCompatActivity implements CalendarPrepareCallback {

    private MonthCalendar monthCalendar;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_month_calendar_layout);

        events.add(new EventModel());

        monthCalendar = (MonthCalendar) findViewById(R.id.monthCalendar);

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
        builder.setDisplayDaysOutOfMonth(false);
        monthCalendar.setCalendarPrepareCallback(this);
        monthCalendar.prepareCalendar(builder.build());

        monthCalendar.setOnDayChangeListener(new OnDayChangeListener() {
            @Override
            public void onDayChanged(Calendar calendar) {
                Toast.makeText(MonthCalendarActivity.this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        monthCalendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChanged(Calendar calendar) {
                Toast.makeText(MonthCalendarActivity.this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCalendarReady(CalendarController calendar) {
        monthCalendar.displayEvents(events, new DisplayEventCallback() {
            @Override
            public void onEventsDisplayed() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        monthCalendar.releaseCalendar();
    }
}
