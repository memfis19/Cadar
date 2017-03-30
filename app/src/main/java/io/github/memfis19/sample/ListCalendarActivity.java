package io.github.memfis19.sample;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.util.Log;
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
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.process.EventsProcessorCallback;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.EventDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.MonthDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.WeekDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.EventDecoratorFactory;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.MonthDecoratorFactory;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory.WeekDecoratorFactory;
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

        ListCalendarConfiguration.Builder listBuilder = new ListCalendarConfiguration.Builder();

        EventDecoratorFactory eventDecoratorFactory = new EventDecoratorFactory() {
            @Override
            public EventDecorator createEventDecorator(View parent) {
                return new EventDecoratorImpl(parent);
            }
        };

        WeekDecoratorFactory weekDecoratorFactory = new WeekDecoratorFactory() {
            @Override
            public WeekDecorator createWeekDecorator(View parent) {
                return new WeeDecoratorImpl(parent);
            }
        };

        MonthDecoratorFactory monthDecoratorFactory = new MonthDecoratorFactory() {
            @Override
            public MonthDecorator createMonthDecorator(View parent) {
                return new MonthDecoratorImpl(parent);
            }
        };

        listBuilder.setDisplayPeriod(Calendar.MONTH, 3);
        listBuilder.setEventLayout(R.layout.custom_event_layout, eventDecoratorFactory);
        listBuilder.setWeekLayout(R.layout.custom_week_title_layout, weekDecoratorFactory);
        listBuilder.setMonthLayout(R.layout.custom_month_calendar_event_layout, monthDecoratorFactory);
//        listBuilder.setEventsProcessor(new CustomEventProcessor());

        listCalendar.setCalendarPrepareCallback(this);
        listCalendar.prepareCalendar(listBuilder.build());
        listCalendar.setOnEventClickListener(new OnEventClickListener() {
            @Override
            public void onEventClick(Event event, int position) {
                Log.i("onEventClick", String.valueOf(event));
            }

            @Override
            public void onSyncClick(Event event, int position) {
                Log.i("onSyncClick", String.valueOf(event));
            }
        });
    }

    private class MonthDecoratorImpl implements MonthDecorator {

        private ImageView monthBackground;
        private TextView monthTitle;
        private Custom custom;

        public MonthDecoratorImpl(View parent) {
            monthBackground = (ImageView) parent.findViewById(R.id.month_background);
            monthTitle = (TextView) parent.findViewById(R.id.month_label);
        }

        @Override
        public void onBindMonthView(View view, Calendar month) {
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
    }

    private class EventDecoratorImpl implements EventDecorator {

        private TextView textView;

        public EventDecoratorImpl(View parent) {
            textView = (TextView) parent.findViewById(R.id.day_title);
        }

        @Override
        public void onBindEventView(View view, Event event, ListItemModel previous, int position) {
            view.setBackgroundColor(ContextCompat.getColor(ListCalendarActivity.this, R.color.eventBackground));
            textView.setText(event.getEventTitle() + "\n" + event.getEventStartDate());
        }
    }

    private class WeeDecoratorImpl implements WeekDecorator {

        private TextView title;

        public WeeDecoratorImpl(View parent) {
            title = (TextView) parent.findViewById(io.github.memfis19.cadar.R.id.week_title);
        }

        @Override
        public void onBindWeekView(View view, Pair<Calendar, Calendar> period) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(title.getContext().getString(io.github.memfis19.cadar.R.string.calendar_week));
            stringBuilder.append("custom ");
            stringBuilder.append(period.first.get(Calendar.WEEK_OF_YEAR));
            stringBuilder.append(", ");
            stringBuilder.append(DateFormat.format("dd MMM", period.first));
            stringBuilder.append(" - ");
            stringBuilder.append(DateFormat.format("dd MMM", period.second));

            final Spannable date = new SpannableString(stringBuilder.toString());

            title.setText(date);
        }
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
        listCalendar.displayEvents(events, new DisplayEventCallback<Pair<Calendar, Calendar>>() {
            @Override
            public void onEventsDisplayed(Pair<Calendar, Calendar> period) {
                Log.d("", "");
                listCalendar.refresh();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        listCalendar.releaseCalendar();
    }

    class CustomEventProcessor extends EventsProcessor<Pair<Calendar, Calendar>, List<Event>> {

        public CustomEventProcessor() {
            super(false, null, true);
        }

        @Override
        protected void processEventsAsync(final Pair<Calendar, Calendar> target, final EventsProcessorCallback<Pair<Calendar, Calendar>, List<Event>> eventsProcessorCallback) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; ++i) {
                        events.add(new EventModel());
                    }
                    eventsProcessorCallback.onEventsProcessed(target, events);
                }
            }, 3000);
        }
    }
}
