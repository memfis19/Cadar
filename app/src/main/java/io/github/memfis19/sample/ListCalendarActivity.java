package io.github.memfis19.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.EventDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.view.ListCalendar;
import io.github.memfis19.sample.model.EventModel;

/**
 * Created by memfis on 11/23/16.
 */

public class ListCalendarActivity extends AppCompatActivity implements CalendarPrepareCallback {

    private ListCalendar listCalendar;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_calendar_layout);

        events.add(new EventModel());

        listCalendar = (ListCalendar) findViewById(R.id.listCalendar);

        ListCalendarConfiguration.Builder listBuilder = new ListCalendarConfiguration.Builder(this);
        listBuilder.setEventLayout(R.layout.custom_event_layout, new EventDecorator() {
            @Override
            public void onBindEventView(View view, Event event, ListItemModel previous, int position) {
                TextView textView = (TextView) view.findViewById(R.id.day_title);
                view.setBackgroundColor(ContextCompat.getColor(ListCalendarActivity.this, R.color.eventBackground));
                textView.setText(event.getEventTitle() + "\n" + event.getEventStartDate());
            }
        });

        listCalendar.setCalendarPrepareCallback(this);
        listCalendar.prepareCalendar(listBuilder.build());
    }

    @Override
    public void onCalendarReady(CalendarController calendar) {
        listCalendar.displayEvents(events, new DisplayEventCallback() {
            @Override
            public void onEventsDisplayed() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        listCalendar.releaseCalendar();
    }
}
