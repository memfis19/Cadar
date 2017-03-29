package io.github.memfis19.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.factory.EventFactory;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.process.EventsProcessorCallback;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.MonthDayDecorator;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.MonthCalendar;
import io.github.memfis19.sample.model.EventModel;
import io.github.memfis19.sample.process.Ical4JEventCalculator;

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

        MonthDayDecoratorFactory monthDayDecoratorFactory = new MonthDayDecoratorFactory() {
            @Override
            public MonthDayDecorator createMonthDayDecorator(View parent) {
                return new MonthDayDecoratorImpl(parent);
            }
        };

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
        builder.setDisplayPeriod(Calendar.YEAR, 1);
        builder.setDisplayDaysOutOfMonth(false);
        builder.setEventProcessingEnabled(true);
        builder.setEventCalculator(new Ical4JEventCalculator());
        builder.setEventFactory(new EventFactory() {
            @Override
            public Event createEventCopy(Event event) {
                return new EventModel(event);
            }
        });
        builder.setMonthDayLayout(R.layout.custom_month_day_layout, monthDayDecoratorFactory);
//        builder.setEventsProcessor(new CustomProcessor());

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

    private class MonthDayDecoratorImpl implements MonthDayDecorator {

        private TextView day;

        public MonthDayDecoratorImpl(View parent) {
            day = (TextView) parent.findViewById(R.id.day_view);
        }

        @Override
        public void onBindDayView(View view, Calendar monthDay, Calendar month, List<Event> eventList, boolean isSelected, boolean isToday) {
            if (!DateUtils.isSameMonth(month, monthDay)) {
                view.setVisibility(View.GONE);
                return;
            } else view.setVisibility(View.VISIBLE);

            day.setText(String.valueOf(monthDay.get(Calendar.DAY_OF_MONTH)));
            day.setTextColor(Color.WHITE);

            view.setBackgroundColor(Color.TRANSPARENT);
            if (isToday) view.setBackgroundColor(Color.RED);
            if (isSelected) view.setBackgroundColor(Color.MAGENTA);
        }
    }

    @Override
    public void onCalendarReady(CalendarController calendar) {
        monthCalendar.displayEvents(events, new DisplayEventCallback<Calendar>() {
            @Override
            public void onEventsDisplayed(Calendar period) {
                Log.d("", "");

                monthCalendar.refresh();
            }
        });
    }

    class CustomProcessor extends EventsProcessor<Calendar, SparseArray<List<Event>>> {

        public CustomProcessor() {
            super(false, null, true);
        }

        @Override
        protected void processEventsAsync(final Calendar target, final EventsProcessorCallback<Calendar, SparseArray<List<Event>>> eventsProcessorCallback) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; ++i) {
                        events.add(new EventModel());
                    }
                    final SparseArray<List<Event>> calendarEvents = new SparseArray<>();

                    Calendar temp = DateUtils.getCalendarInstance();
                    for (Event event : events) {
                        temp.setTime(event.getEventStartDate());

                        if (!DateUtils.isSameMonth(temp, target)) continue;

                        List<Event> events = calendarEvents.get(temp.get(Calendar.DAY_OF_MONTH), new ArrayList<Event>());
                        events.add(event);

                        calendarEvents.put(temp.get(Calendar.DAY_OF_MONTH), events);
                    }
                    events.clear();

                    eventsProcessorCallback.onEventsProcessed(target, calendarEvents);
                }
            }, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        monthCalendar.releaseCalendar();
    }
}
