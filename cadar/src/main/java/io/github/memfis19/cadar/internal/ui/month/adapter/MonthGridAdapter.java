package io.github.memfis19.cadar.internal.ui.month.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.internal.helper.ScrollManager;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.ui.month.MonthCalendarHelper;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;
import io.github.memfis19.cadar.internal.ui.month.adapter.holder.MonthDayHolder;
import io.github.memfis19.cadar.internal.utils.DateUtils;


/**
 * Created by memfis on 7/13/16.
 */
class MonthGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnDayChangeListener {

    private static final String TAG = "MonthGridAdapter";

    private Calendar month;
    private int monthIntValue;

    private List<Calendar> monthDays = new ArrayList<>();
    private SparseArray<List<Event>> calendarEvents = new SparseArray();
    private List<MonthDayHolder> monthDayHolders = new ArrayList<>();

    private boolean containsToday = false;
    private boolean containsSelected = false;
    private boolean displayDaysOutOfMonth = true;

    @LayoutRes
    private int monthDayLayoutId;
    private MonthDayDecoratorFactory monthDayDecoratorFactory;

    private EventsProcessor eventsAsyncProcessor;
    private OnDayChangeListener onDateChangeListener;

    MonthGridAdapter(Calendar month, List<Calendar> monthItems, boolean displayDaysOutOfMonth) {
        this(month, monthItems);
        this.displayDaysOutOfMonth = displayDaysOutOfMonth;
    }

    MonthGridAdapter(Calendar month, List<Calendar> monthItems) {
        this.month = month;
        this.month.set(Calendar.DAY_OF_MONTH, 1);
        this.monthDays = monthItems;

        monthIntValue = month.get(Calendar.MONTH);

        containsToday = monthIntValue == MonthCalendarHelper.getToday().get(Calendar.MONTH)
                && month.get(Calendar.YEAR) == MonthCalendarHelper.getToday().get(Calendar.YEAR);

        containsSelected = monthIntValue == MonthCalendarHelper.getSelectedDay().get(Calendar.MONTH)
                && month.get(Calendar.YEAR) == MonthCalendarHelper.getSelectedDay().get(Calendar.YEAR);
    }

    void requestDisplayEvents() {
        if (ScrollManager.getInstance().getCurrentScrollState() == ScrollManager.SCROLL_STATE_IDLE)
            requestMonthEvents();
    }

    void setMonthDayLayoutId(@LayoutRes int monthDayLayoutId) {
        this.monthDayLayoutId = monthDayLayoutId;
    }

    void setMonthDayDecoratorFactory(MonthDayDecoratorFactory monthDayDecoratorFactory) {
        this.monthDayDecoratorFactory = monthDayDecoratorFactory;
    }

    void setEventsAsyncProcessor(EventsProcessor eventsAsyncProcessor) {
        this.eventsAsyncProcessor = eventsAsyncProcessor;
    }

    void setOnDateChangeListener(OnDayChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public Calendar getMonth() {
        return month;
    }

    void requestMonthEvents() {
        eventsAsyncProcessor.queueEventsProcess(month);
    }

    void displayEventsForMonth(SparseArray<List<Event>> calendarEvents) {
        this.calendarEvents = calendarEvents;
        notifyHolders();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View dayOfMonthView = LayoutInflater.from(parent.getContext()).inflate(monthDayLayoutId, parent, false);
        MonthDayHolder monthDayHolder = new MonthDayHolder(dayOfMonthView, displayDaysOutOfMonth, this, monthDayDecoratorFactory);
        monthDayHolders.add(monthDayHolder);
        return monthDayHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MonthDayHolder) holder).bindView(
                monthDays.get(position),
                month,
                monthIntValue == monthDays.get(position).get(Calendar.MONTH) ?
                        calendarEvents.get(monthDays.get(position).get(Calendar.DAY_OF_MONTH), null) : null,
                containsSelected && DateUtils.isSameDay(MonthCalendarHelper.getSelectedDay(), monthDays.get(position)),
                containsToday && DateUtils.isToday(monthDays.get(position))
        );
    }

    @Override
    public int getItemCount() {
        return monthDays.size();
    }

    @Override
    public void onDayChanged(Calendar calendar) {
        if (onDateChangeListener != null) onDateChangeListener.onDayChanged(calendar);
    }

    void notifyMonthSetChanged() {
        containsSelected = month.get(Calendar.MONTH) == MonthCalendarHelper.getSelectedDay().get(Calendar.MONTH)
                && month.get(Calendar.YEAR) == MonthCalendarHelper.getSelectedDay().get(Calendar.YEAR);
        notifyHolders();
    }

    private void notifyHolders() {
        try {
            if (!monthDayHolders.isEmpty()) {
                for (int i = 0; i < getItemCount(); ++i) {
                    onBindViewHolder(monthDayHolders.get(i), i);
                }
            }
        } catch (Exception e) {
            //ignore exception usually happens at rotation
        }
    }
}
