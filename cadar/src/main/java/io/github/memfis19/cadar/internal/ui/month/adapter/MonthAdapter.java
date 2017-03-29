package io.github.memfis19.cadar.internal.ui.month.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.process.EventsProcessorCallback;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;

/**
 * Created by memfis on 7/19/16.
 */
public class MonthAdapter extends PagerAdapter implements OnDayChangeListener,
        EventsProcessorCallback<Calendar, SparseArray<List<Event>>>, MonthHandlerThread.AdapterPrepareListener {

    private static final String TAG = "MonthAdapter";

    private static int CAPACITY = 11;
    private static int CAPACITY_STEP = 11;

    private Context context;
    private LayoutInflater inflater;
    private MonthCalendarConfiguration monthCalendarConfiguration;

    private MonthHandlerThread monthHandlerThread;
    private Handler backgroundHandler;
    private Handler uiHandler;

    private Set<Event> events = new HashSet<>();
    private List<Event> eventList = new ArrayList<>();

    private EventsProcessor<Calendar, SparseArray<List<Event>>> eventsAsyncProcessor;
    private OnDayChangeListener onDateChangeListener;
    private DisplayEventCallback<Calendar> callback;
    private MonthGridCallback monthGridCallback;

    public interface MonthGridCallback {
        void onMonthGridReady(Calendar month);
    }

    @LayoutRes
    private int monthDayLayoutId;
    private MonthDayDecoratorFactory monthDayDecoratorFactory;

    private List<RecyclerView> monthFragments = new ArrayList<>();

    private Calendar initialDate = DateUtils.getCalendarInstance();

    private static int INITIAL_POSITION = 0;

    public MonthAdapter(Context context,
                        MonthHandlerThread monthHandlerThread,
                        EventsProcessor<Calendar, SparseArray<List<Event>>> eventsAsyncProcessor,
                        MonthCalendarConfiguration monthCalendarConfiguration) {

        this(context, eventsAsyncProcessor, monthCalendarConfiguration.getMonthLayoutId(), monthCalendarConfiguration.getMonthDayDecoratorFactory());

        this.monthCalendarConfiguration = monthCalendarConfiguration;

        Calendar calendar = DateUtils.getCalendarInstance();
        calendar = DateUtils.setTimeToMidnight(calendar);

        Calendar startPeriod = DateUtils.setTimeToMonthStart((Calendar) calendar.clone());
        startPeriod.set(monthCalendarConfiguration.getPeriodType(), calendar.get(monthCalendarConfiguration.getPeriodType()) - monthCalendarConfiguration.getPeriodValue());

        Calendar endPeriod = DateUtils.setTimeToMonthStart((Calendar) calendar.clone());
        endPeriod.set(monthCalendarConfiguration.getPeriodType(), calendar.get(monthCalendarConfiguration.getPeriodType()) + monthCalendarConfiguration.getPeriodValue());

        CAPACITY = DateUtils.monthBetweenPure(startPeriod.getTime(), endPeriod.getTime());
        CAPACITY_STEP = CAPACITY;

        this.initialDate = monthCalendarConfiguration.getInitialDay();
        this.monthHandlerThread = monthHandlerThread;
        this.monthHandlerThread.setAdapterPrepareListener(this);

        backgroundHandler = new Handler(monthHandlerThread.getLooper());
        uiHandler = new Handler(Looper.getMainLooper());
    }

    MonthAdapter(Context context,
                 EventsProcessor<Calendar, SparseArray<List<Event>>> eventsAsyncProcessor,
                 @LayoutRes int monthDayLayoutId,
                 MonthDayDecoratorFactory monthDayDecoratorFactory) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        this.eventsAsyncProcessor = eventsAsyncProcessor;
        this.monthDayLayoutId = monthDayLayoutId;
        this.monthDayDecoratorFactory = monthDayDecoratorFactory;

        this.eventsAsyncProcessor.setEventsProcessorCallback(this);
    }

    public void setMonthGridCallback(MonthGridCallback monthGridCallback) {
        this.monthGridCallback = monthGridCallback;
    }

    public void setOnDateChangeListener(OnDayChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public static int getInitialPosition(Calendar initialDate) {
        if (INITIAL_POSITION == 0) {
            INITIAL_POSITION = CAPACITY / 2 + 1;
        }
        return INITIAL_POSITION;
    }

    public Calendar getDayForPosition(int position) {
        Calendar month = (Calendar) initialDate.clone();
        int shiftValue = position - getInitialPosition(initialDate);
        month.add(Calendar.MONTH, shiftValue);
        return month;
    }

    @Override
    public int getItemPosition(Object object) {
        return getDayPosition(((MonthGridAdapter) ((RecyclerView) object).getAdapter()).getMonth());
    }

    @Override
    public int getCount() {
        return CAPACITY;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.month_grid_layout, collection, false);
        monthFragments.add(recyclerView);

        int shiftValue = position - getInitialPosition(initialDate);

        monthHandlerThread.queuePreparation(initialDate, shiftValue, recyclerView);

        collection.addView(recyclerView);
        return recyclerView;
    }

    public int getCountOfInstantiateItems() {
        return monthFragments.size();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        monthFragments.remove(view);

        collection.removeView((View) view);
    }

    public void refresh() {
        this.callback = null;
        displayEvents();
    }

    public void displayEvents(List<Event> eventList, DisplayEventCallback<Calendar> callback) {
        events.clear();
        events.addAll(eventList);

        this.eventList.clear();
        this.eventList.addAll(events);

        this.callback = callback;

        eventsAsyncProcessor.setEvents(this.eventList);
        displayEvents();
    }

    public void addEvents(List<Event> eventList) {
        events.addAll(eventList);

        this.eventList.clear();
        this.eventList.addAll(events);

        eventsAsyncProcessor.setEvents(this.eventList);
        displayEvents();
    }

    public void displayEvents() {
        for (RecyclerView recyclerView : monthFragments) {
            if (recyclerView.getAdapter() != null)
                ((MonthGridAdapter) recyclerView.getAdapter()).requestMonthEvents();
        }
    }

    public void notifyDayWasSelected() {
        for (RecyclerView recyclerView : monthFragments) {
            if (recyclerView.getAdapter() != null)
                ((MonthGridAdapter) recyclerView.getAdapter()).notifyMonthSetChanged();
        }
    }

    public int getDayPosition(Calendar day) {
        int shiftValue = DateUtils.monthBetweenPure(initialDate.getTime(), day.getTime());
        return getInitialPosition(initialDate) + shiftValue;
    }

    @Override
    public void onDayChanged(Calendar calendar) {
        notifyDayWasSelected();

        if (onDateChangeListener != null) onDateChangeListener.onDayChanged(calendar);
    }

    @Override
    public void onEventsProcessed(Calendar calendar, SparseArray<List<Event>> calendarEvents) {
        for (RecyclerView recyclerView : monthFragments) {
            if (recyclerView.getAdapter() != null)
                if (((MonthGridAdapter) recyclerView.getAdapter()).getMonth().equals(calendar)) {
                    ((MonthGridAdapter) recyclerView.getAdapter()).displayEventsForMonth(calendarEvents);
                    if (callback != null) callback.onEventsDisplayed(calendar);
                }
        }
    }

    public final static int FORWARD = 1;
    public final static int BACKWARD = 2;

    public void increaseSize(int direction) {
        if (direction == FORWARD) {
            CAPACITY += CAPACITY_STEP;

            notifyDataSetChanged();
        } else if (direction == BACKWARD) {
            CAPACITY += CAPACITY_STEP;
            INITIAL_POSITION += CAPACITY_STEP;

            notifyDataSetChanged();
        }
    }

    @Override
    public void onReadyAdapter(Calendar month, List<Calendar> monthDays, RecyclerView recyclerView) {
        MonthGridAdapter monthGridAdapter = new MonthGridAdapter(month, monthDays, monthCalendarConfiguration.isDisplayDaysOutOfMonth());

        monthGridAdapter.setOnDateChangeListener(this);
        monthGridAdapter.setEventsAsyncProcessor(eventsAsyncProcessor);
        monthGridAdapter.setMonthDayLayoutId(monthDayLayoutId);
        monthGridAdapter.setMonthDayDecoratorFactory(monthDayDecoratorFactory);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(monthGridAdapter);

        if (monthGridCallback != null) monthGridCallback.onMonthGridReady(month);
//        monthGridAdapter.requestDisplayEvents();
    }
}
