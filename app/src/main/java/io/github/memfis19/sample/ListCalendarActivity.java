package io.github.memfis19.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.EventDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.MonthDecorator;
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
        listBuilder.setMonthLayout(R.layout.custom_month_calendar_event_layout, new MonthDecorator() {

            Custom custom;

            @Override
            public void onBindMonthView(View view, Calendar month) {
                final ImageView monthBackground = (ImageView) view.findViewById(R.id.month_background);
                final TextView monthTitle = (TextView) view.findViewById(R.id.month_label);

                custom.setMonthBackground(monthBackground);

                monthBackground.setImageDrawable(null);

                final int backgroundId = getBackgroundId(month.get(Calendar.MONTH));
                monthTitle.setText(month.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
                Picasso.with(monthTitle.getContext().getApplicationContext()).load(backgroundId).into(monthBackground, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (Build.VERSION.SDK_INT > 13) {
                            monthBackground.setScrollX(0);
                            monthBackground.setScrollY(0);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @NonNull
            @Override
            public RecyclerView.OnScrollListener getScrollListener() {
                custom = new Custom();
                return custom;
            }
        });

        listCalendar.setCalendarPrepareCallback(this);
        listCalendar.prepareCalendar(listBuilder.build());
    }

    private int getBackgroundId(int month) {
        int backgroundId = io.github.memfis19.cadar.R.drawable.bkg_12_december;

        if (month == Calendar.JANUARY) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_01_january;
        } else if (month == Calendar.FEBRUARY) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_02_february;
        } else if (month == Calendar.MARCH) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_03_march;
        } else if (month == Calendar.APRIL) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_04_april;
        } else if (month == Calendar.MAY) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_05_may;
        } else if (month == Calendar.JUNE) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_06_june;
        } else if (month == Calendar.JULY) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_07_july;
        } else if (month == Calendar.AUGUST) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_08_august;
        } else if (month == Calendar.SEPTEMBER) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_09_september;
        } else if (month == Calendar.OCTOBER) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_10_october;
        } else if (month == Calendar.NOVEMBER) {
            backgroundId = io.github.memfis19.cadar.R.drawable.bkg_11_november;
        }

        return backgroundId;
    }

    private class Custom extends RecyclerView.OnScrollListener {

        private View monthBackground;

        Custom() {
        }

        public void setMonthBackground(View monthBackground) {
            this.monthBackground = monthBackground;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (monthBackground != null) monthBackground.scrollBy(dx, (-1) * (dy / 10));
        }
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
