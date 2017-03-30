package io.github.memfis19.cadar.view;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

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
public class ListCalendar extends RecyclerView implements CalendarController<ListCalendarConfiguration, Pair<Calendar, Calendar>> {

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
        setLayoutManager(new CalendarLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
    public void displayEvents(List<Event> list, DisplayEventCallback<Pair<Calendar, Calendar>> callback) {
        this.eventList = list;
        if (listAdapter != null) listAdapter.displayEvents(list, callback);
    }

    public void refresh() {
        if (listAdapter != null) listAdapter.displayEvents();
    }

    public void addEvent(Event event) {
        listAdapter.addEvent(event);
    }

    public void addEvents(List<Event> events) {
        listAdapter.addEvents(events);
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

    /**
     * Check if this view can be scrolled horizontally in a certain direction.
     *
     * @param direction Negative to check scrolling left, positive to check scrolling right.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public boolean canScrollHorizontally(int direction) {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    /**
     * Check if this view can be scrolled vertically in a certain direction.
     *
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public boolean canScrollVertically(int direction) {
        final int offset = computeVerticalScrollOffset();
        final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    private class CalendarLayoutManager extends LinearLayoutManager {

        public CalendarLayoutManager(Context context) {
            super(context);
        }

        public CalendarLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public CalendarLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,
                                      RecyclerView.State state) {
            try {
                int value = super.scrollVerticallyBy(dy, recycler, state);
                Log.i("", "");
                return value;
            } catch (Exception error) {
                Log.e(ListCalendar.TAG, "IndexOutOfBoundsException in RecyclerView happens", error);
            }
            return 0;
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        /***
         * Workaround for know RV issue
         * @link https://code.google.com/p/android/issues/detail?id=77846#c10
         */
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e(ListCalendar.TAG, "IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }

}
