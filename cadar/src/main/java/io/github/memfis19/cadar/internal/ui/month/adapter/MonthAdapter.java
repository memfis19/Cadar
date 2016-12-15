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
import java.util.List;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.internal.process.BaseEventsAsyncProcessor;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;

/**
 * Created by memfis on 7/19/16.
 */
public class MonthAdapter extends PagerAdapter implements OnDayChangeListener,
        BaseEventsAsyncProcessor.EventsProcessorListener<Calendar, SparseArray<List<Event>>>, MonthHandlerThread.AdapterPrepareListener {

    private static final String TAG = "MonthAdapter";

    private final static int MAX_YEARS_BEFORE_CURRENT = 1;
    private final static int CAPACITY = 11;

    private Context context;
    private LayoutInflater inflater;
    private MonthCalendarConfiguration monthCalendarConfiguration;

    private MonthHandlerThread monthHandlerThread;
    private Handler backgroundHandler;
    private Handler uiHandler;

    private BaseEventsAsyncProcessor eventsAsyncProcessor;
    private OnDayChangeListener onDateChangeListener;

    @LayoutRes
    private int monthDayLayoutId;
    private MonthDayDecoratorFactory monthDayDecoratorFactory;

    private List<RecyclerView> monthFragments = new ArrayList<>();

    private Calendar initialDate = DateUtils.getCalendarInstance();

    private static int INITIAL_POSITION = 0;

    public MonthAdapter(Context context,
                        MonthHandlerThread monthHandlerThread,
                        BaseEventsAsyncProcessor eventsAsyncProcessor,
                        MonthCalendarConfiguration monthCalendarConfiguration) {

        this(context, eventsAsyncProcessor, monthCalendarConfiguration.getMonthLayoutId(), monthCalendarConfiguration.getMonthDayDecoratorFactory());

        this.monthCalendarConfiguration = monthCalendarConfiguration;

        this.initialDate = monthCalendarConfiguration.getInitialDay();
        this.monthHandlerThread = monthHandlerThread;
        this.monthHandlerThread.setAdapterPrepareListener(this);

        backgroundHandler = new Handler(monthHandlerThread.getLooper());
        uiHandler = new Handler(Looper.getMainLooper());
    }

    MonthAdapter(Context context,
                 BaseEventsAsyncProcessor eventsAsyncProcessor,
                 @LayoutRes int monthDayLayoutId,
                 MonthDayDecoratorFactory monthDayDecoratorFactory) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        this.eventsAsyncProcessor = eventsAsyncProcessor;
        this.monthDayLayoutId = monthDayLayoutId;
        this.monthDayDecoratorFactory = monthDayDecoratorFactory;

        this.eventsAsyncProcessor.setEventsProcessorListener(this);
    }

    public void setOnDateChangeListener(OnDayChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public static int getInitialPosition(Calendar initialDate) {
        if (INITIAL_POSITION == 0) {
            INITIAL_POSITION = MAX_YEARS_BEFORE_CURRENT * 12 + initialDate.get(Calendar.MONTH);
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
        return CAPACITY * 12;
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

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        monthFragments.remove(view);

        collection.removeView((View) view);
    }

    public void displayEvents(List<Event> eventList) {
        eventsAsyncProcessor.setEvents(eventList);
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
                }
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

        monthGridAdapter.requestDisplayEvents();
    }
}
