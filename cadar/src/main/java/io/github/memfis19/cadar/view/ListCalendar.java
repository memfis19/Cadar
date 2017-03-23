package io.github.memfis19.cadar.view;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.ui.list.adapter.ListAdapter;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;
import io.github.memfis19.cadar.internal.utils.CalendarHelper;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;


/**
 * Created by memfis on 9/5/16.
 */
public class ListCalendar extends RecyclerView implements CalendarController<ListCalendarConfiguration> {

    private static final String TAG = "ListCalendar";

    protected HandlerThread backgroundThread;
    protected Handler backgroundHandler;
    protected Handler uiHandler = new Handler(Looper.getMainLooper());

    private List<Event> eventList;
    private ListAdapter listAdapter;

    private OnSetLayoutManagerListener onSetLayoutManagerListener;

    private ListCalendarConfiguration configuration;

    private OnMonthChangeListener monthChangeListener;
    private OnDayChangeListener dayChangeListener;
    private OnEventClickListener onEventClickListener;

    private Calendar selectedDay = DateUtils.getCalendarInstance();

    public CalendarPrepareCallback calendarPrepareCallback;

    public ListCalendar(Context context) {
        super(context);
    }

    public ListCalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void prepareCalendar(final ListCalendarConfiguration configuration) {
        this.configuration = configuration;
        backgroundThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                prepareInitialData();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initListWithAdapter();
                    }
                });
            }
        });
    }

    @WorkerThread
    private void prepareInitialData() {
        Calendar calendar = DateUtils.getCalendarInstance();
        calendar = DateUtils.setTimeToMidnight(calendar);

        final List<ListItemModel> listItemModels = new ArrayList<>();

        Calendar startPeriod = DateUtils.setTimeToMonthStart((Calendar) calendar.clone());
        startPeriod.set(configuration.getPeriodType(), calendar.get(configuration.getPeriodType()) - configuration.getPeriodValue());

        Calendar endPeriod = DateUtils.setTimeToMonthStart((Calendar) calendar.clone());
        endPeriod.set(configuration.getPeriodType(), calendar.get(configuration.getPeriodType()) + configuration.getPeriodValue());

        int capacityMonth = DateUtils.monthBetweenPure(startPeriod.getTime(), endPeriod.getTime());
        CalendarHelper.prepareListItems(listItemModels, startPeriod, capacityMonth);

        listAdapter = new ListAdapter(
                configuration,
                ListCalendar.this,
                listItemModels,
                eventList,
                startPeriod,
                endPeriod,
                backgroundHandler,
                uiHandler,
                monthChangeListener,
                dayChangeListener);

        listAdapter.setOnEventClickListener(onEventClickListener);

        onSetLayoutManagerListener = listAdapter;
    }

    @UiThread
    private void initListWithAdapter() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter(listAdapter);
        scrollToPosition(listAdapter.getDatePosition(DateUtils.setTimeToMidnight(selectedDay)));
        if (calendarPrepareCallback != null)
            calendarPrepareCallback.onCalendarReady(this);
    }

    public void setCalendarPrepareCallback(CalendarPrepareCallback calendarPrepareCallback) {
        this.calendarPrepareCallback = calendarPrepareCallback;
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public interface OnSetLayoutManagerListener {
        void onSetLayoutManager(LayoutManager layout);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        onSetLayoutManagerListener.onSetLayoutManager(layout);
    }

    @Override
    public void releaseCalendar() {
        backgroundThread.quit();
        listAdapter.release();
    }

    @Override
    public void setSelectedDay(Calendar selectedDay, boolean scrollToSelectedDay) {
        this.selectedDay = selectedDay;
        if (listAdapter != null)
            listAdapter.setSelectedDay(selectedDay);
    }

    @Override
    public void displayEvents(List list, DisplayEventCallback callback) {
        this.eventList = list;
        if (listAdapter != null) listAdapter.displayEvents(list, callback);
    }

    public void addEvent(Event event) {
        listAdapter.addEvent(event);
    }

    public void editEvent(Event event) {
        listAdapter.editEvent(event);
    }

    public void removeEvent(Event event) {
        listAdapter.removeEvent(event);
    }

    public void setOnMonthChangeListener(OnMonthChangeListener monthChangeListener) {
        this.monthChangeListener = monthChangeListener;
    }

    public void setOnDayChangeListener(OnDayChangeListener dayChangeListener) {
        this.dayChangeListener = dayChangeListener;
    }

    public void setSelectedMonth(Calendar selectedMonth) {
        listAdapter.setSelectedMonth(selectedMonth);
    }

}
