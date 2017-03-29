package io.github.memfis19.cadar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.helper.ScrollManager;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.process.MonthEventsProcessor;
import io.github.memfis19.cadar.internal.ui.month.MonthCalendarHelper;
import io.github.memfis19.cadar.internal.ui.month.adapter.MonthAdapter;
import io.github.memfis19.cadar.internal.ui.month.adapter.MonthHandlerThread;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;


/**
 * Created by memfis on 7/14/16.
 */
public class MonthCalendar extends LinearLayout implements ViewPager.OnPageChangeListener,
        ScrollManager.OnScrollChanged, CalendarController<MonthCalendarConfiguration, Calendar>, ViewTreeObserver.OnPreDrawListener {

    private static final String TAG = "MonthView3";

    private Calendar currentMonth;
    private int currentPosition = 0;

    private MonthHandlerThread monthHandlerThread;
    private EventsProcessor<Calendar, SparseArray<List<Event>>> eventsAsyncProcessor;

    private LayoutInflater layoutInflater;
    private ViewGroup monthHeaderView;
    private ViewPager monthGridView;

    private MonthAdapter monthAdapter;

    private MonthCalendarConfiguration monthCalendarConfiguration;
    private SimpleDateFormat weekDayFormatter;

    private Calendar workingCalendar = DateUtils.getCalendarInstance();

    private OnDayChangeListener onDateChangeListener;
    private OnMonthChangeListener onMonthChangeListener;
    private CalendarPrepareCallback calendarPrepareCallback;

    protected HandlerThread backgroundThread;
    protected Handler backgroundHandler;
    protected Handler uiHandler = new Handler(Looper.getMainLooper());

    private int amountOfReadyMonths = 0;

    public void setCalendarPrepareCallback(CalendarPrepareCallback calendarPrepareCallback) {
        this.calendarPrepareCallback = calendarPrepareCallback;
    }

    public MonthCalendar(Context context) {
        super(context);
        init();
    }

    public MonthCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MonthCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        layoutInflater = LayoutInflater.from(getContext());
        inflate(getContext(), R.layout.month_calendar_layout, this);

        monthHeaderView = (ViewGroup) findViewById(R.id.month_header);
        monthGridView = (ViewPager) findViewById(R.id.month_grid);
    }

    @Override
    public void prepareCalendar(final MonthCalendarConfiguration monthCalendarConfiguration) {
        this.monthCalendarConfiguration = monthCalendarConfiguration;

        backgroundThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        monthHandlerThread = new MonthHandlerThread();
        monthHandlerThread.start();
        monthHandlerThread.getLooper();

        if (monthCalendarConfiguration.getEventsProcessor() != null)
            eventsAsyncProcessor = monthCalendarConfiguration.getEventsProcessor();
        else
            eventsAsyncProcessor = new MonthEventsProcessor(monthCalendarConfiguration.isEventProcessingEnabled(), monthCalendarConfiguration.getEventCalculator());
        eventsAsyncProcessor.setEventProcessor(monthCalendarConfiguration.getEventCalculator());
        eventsAsyncProcessor.start();
        eventsAsyncProcessor.getLooper();

        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!monthCalendarConfiguration.isWeekDayTitleTranslationEnabled())
                    weekDayFormatter = new SimpleDateFormat("EEE", monthCalendarConfiguration.getLocale());

                int pos = monthCalendarConfiguration.getFirstDayOfWeek();

                for (int i = pos; i < pos + 7; ++i) {
                    initHeaderItem(monthHeaderView, i);
                }

                monthAdapter = new MonthAdapter(monthCalendarConfiguration.getContext(), monthHandlerThread,
                        eventsAsyncProcessor, monthCalendarConfiguration);
                monthAdapter.setOnDateChangeListener(onDateChangeListener);
                monthAdapter.setMonthGridCallback(new MonthAdapter.MonthGridCallback() {
                    @Override
                    public void onMonthGridReady(Calendar month) {
                        amountOfReadyMonths++;

                        if (amountOfReadyMonths == monthAdapter.getCountOfInstantiateItems() && calendarPrepareCallback != null) {
                            calendarPrepareCallback.onCalendarReady(MonthCalendar.this);
                            monthAdapter.setMonthGridCallback(null);
                        }
                    }
                });

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        monthGridView.setAdapter(monthAdapter);
                        monthGridView.setOffscreenPageLimit(1);

                        monthGridView.setCurrentItem(MonthAdapter.getInitialPosition(monthCalendarConfiguration.getInitialDay()), false);

                        ScrollManager.getInstance().subscribeForScrollStateChanged(MonthCalendar.this);
                        monthGridView.addOnPageChangeListener(MonthCalendar.this);

                        getViewTreeObserver().addOnPreDrawListener(MonthCalendar.this);
                    }
                });
            }
        });
    }


    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);

        return true;
    }

    @Override
    public void releaseCalendar() {
        monthHandlerThread.quitSafely();
        eventsAsyncProcessor.quitSafely();
        ScrollManager.getInstance().unSubscribeForScrollStateChanged(this);
        MonthCalendarHelper.updateSelectedDay(MonthCalendarHelper.getToday());
    }

    public void setOnDayChangeListener(OnDayChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
        if (monthAdapter != null) monthAdapter.setOnDateChangeListener(onDateChangeListener);
    }

    public void setOnMonthChangeListener(OnMonthChangeListener onMonthChangeListener) {
        this.onMonthChangeListener = onMonthChangeListener;
    }

    @Override
    public void setSelectedDay(Calendar selectedDay, boolean scrollToSelectedDay) {
        MonthCalendarHelper.updateSelectedDay(selectedDay);
        if (scrollToSelectedDay) {
            monthGridView.setCurrentItem(monthAdapter.getDayPosition(selectedDay), false);
        }
        ScrollManager.getInstance().unSubscribeForScrollStateChanged(this);
        monthAdapter.notifyDayWasSelected();
        ScrollManager.getInstance().subscribeForScrollStateChanged(this);
    }

    private void initHeaderItem(final ViewGroup headerItem, int position) {
        int correctPosition = (position > 7) ? (position % 7) : position;
        View headerView = null;
        switch (correctPosition) {
            case Calendar.SUNDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.SUNDAY, headerItem);
                break;
            case Calendar.MONDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.MONDAY, headerItem);
                break;
            case Calendar.TUESDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.TUESDAY, headerItem);
                break;
            case Calendar.WEDNESDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.WEDNESDAY, headerItem);
                break;
            case Calendar.THURSDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.THURSDAY, headerItem);
                break;
            case Calendar.FRIDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.FRIDAY, headerItem);
                break;
            case Calendar.SATURDAY:
                headerView = createDayOfWeekHeader(
                        Calendar.SATURDAY, headerItem);
                break;
            default:
                break;
        }

        if (headerView != null) {
            final View view = headerView;
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    headerItem.addView(view);
                }
            });
        }
    }

    private View createDayOfWeekHeader(int calendarWeekDay, ViewGroup parent) {
        View weekDayView = layoutInflater.inflate(monthCalendarConfiguration.getWeekTitleLayoutId(), parent, false);

        String dayOfWeekTitle = "";

        if (monthCalendarConfiguration.isWeekDayTitleTranslationEnabled()) {
            if (calendarWeekDay == Calendar.MONDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getMondayTitle());
            else if (calendarWeekDay == Calendar.TUESDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getTuesdayTitle());
            else if (calendarWeekDay == Calendar.WEDNESDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getWednesdayTitle());
            else if (calendarWeekDay == Calendar.THURSDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getThursdayTitle());
            else if (calendarWeekDay == Calendar.FRIDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getFridayTitle());
            else if (calendarWeekDay == Calendar.SATURDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getSaturdayTitle());
            else if (calendarWeekDay == Calendar.SUNDAY)
                dayOfWeekTitle = getContext().getString(monthCalendarConfiguration.getSundayTitle());
        } else {
            workingCalendar.set(Calendar.DAY_OF_WEEK, calendarWeekDay);
            dayOfWeekTitle = weekDayFormatter.format(workingCalendar.getTime());
        }

        if (monthCalendarConfiguration.getWeekDayDecorator() != null) {
            monthCalendarConfiguration.getWeekDayDecorator().onBindWeekDayView(weekDayView, calendarWeekDay, dayOfWeekTitle);
        } else {
            ((TextView) weekDayView).setGravity(Gravity.CENTER);
            ((TextView) weekDayView).setText(dayOfWeekTitle);
        }

        return weekDayView;
    }

    public void refresh() {
        monthAdapter.refresh();
    }

    @Override
    public void displayEvents(List<Event> eventList, DisplayEventCallback<Calendar> callback) {
        if (monthAdapter != null)
            monthAdapter.displayEvents(eventList, callback);
    }

    public void addEvents(List<Event> eventList) {
        if (monthAdapter != null)
            monthAdapter.addEvents(eventList);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.currentPosition = position;

        if (currentPosition == 0) {
            monthAdapter.increaseSize(MonthAdapter.BACKWARD);
        } else if (currentPosition == monthAdapter.getCount() - 1) {
            monthAdapter.increaseSize(MonthAdapter.FORWARD);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            ScrollManager.getInstance().notifyScrollStateChanged(ScrollManager.SCROLL_STATE_DRAGGING);
        } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
            ScrollManager.getInstance().notifyScrollStateChanged(ScrollManager.SCROLL_STATE_SETTLING);
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
            ScrollManager.getInstance().notifyScrollStateChanged(ScrollManager.SCROLL_STATE_IDLE);
            checkMonth();
        }
    }

    private void checkMonth() {
        Calendar selectedMonth = monthAdapter.getDayForPosition(currentPosition);
        if (currentMonth != selectedMonth) {
            currentMonth = selectedMonth;
            if (onMonthChangeListener != null) {
                onMonthChangeListener.onMonthChanged(currentMonth);
            }
        }
    }

    @Override
    public void onScrollStateChanged(@ScrollManager.ScrollState int scrollState) {
        if (scrollState == ScrollManager.SCROLL_STATE_IDLE) {
            monthAdapter.displayEvents();
        }
    }
}
