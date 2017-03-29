package io.github.memfis19.cadar.internal.ui.list.adapter;

import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.process.EventsProcessor;
import io.github.memfis19.cadar.internal.process.EventsProcessorCallback;
import io.github.memfis19.cadar.internal.process.ListEventsProcessor;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.EventHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.ListHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.MonthHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.WeekHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;
import io.github.memfis19.cadar.internal.ui.list.event.EndlessRecyclerViewScrollListener;
import io.github.memfis19.cadar.internal.utils.CalendarHelper;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.view.ListCalendar;

/**
 * Created by memfis on 9/5/16.
 */
public class ListAdapter extends RecyclerView.Adapter<ListHolder>
        implements ListCalendar.OnSetLayoutManagerListener {

    private static final String TAG = "ListAdapter";

    private static final int THRESHOLD = 5;

    private ListCalendar calendarListView;
    private List<ListItemModel> listItemModels;

    private Handler backgroundHandler;
    private Handler uiHandler;

    private EventsProcessor<Pair<Calendar, Calendar>, List<Event>> listEventsAsyncProcessor;
    private int position;

    private Calendar startPeriod;
    private Calendar endPeriod;
    private ListCalendarConfiguration configuration;

    private OnMonthChangeListener monthChangeListener;
    private OnDayChangeListener dayChangeListener;
    private OnEventClickListener onEventClickListener;

    private List<Event> eventList = new ArrayList<>();
    private DisplayEventCallback<Pair<Calendar, Calendar>> callback;

    public ListAdapter(ListCalendarConfiguration configuration,
                       ListCalendar calendarListView,
                       List<ListItemModel> listItemModels,
                       List<Event> eventList,
                       Calendar startPeriod, Calendar endPeriod,
                       Handler backgroundHandler, Handler uiHandler,
                       OnMonthChangeListener monthChangeLister,
                       OnDayChangeListener dayChangeListener) {

        this.calendarListView = calendarListView;
        this.listItemModels = listItemModels;
        this.backgroundHandler = backgroundHandler;
        this.uiHandler = uiHandler;

        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.configuration = configuration;
        this.eventList = eventList;

        this.monthChangeListener = monthChangeLister;
        this.dayChangeListener = dayChangeListener;

        if (configuration.getEventsProcessor() != null)
            listEventsAsyncProcessor = configuration.getEventsProcessor();
        else
            listEventsAsyncProcessor = new ListEventsProcessor(configuration.isEventProcessingEnabled(), configuration.getEventCalculator());

        listEventsAsyncProcessor.setEventProcessor(configuration.getEventCalculator());
        listEventsAsyncProcessor.setEvents(eventList);
        listEventsAsyncProcessor.start();
        listEventsAsyncProcessor.getLooper();
    }

    private void loadMoreEvents() {
        Calendar tmp = (Calendar) endPeriod.clone();
        endPeriod.add(configuration.getPeriodType(), configuration.getPeriodValue());
        Calendar start = tmp;
        CalendarHelper.prepareListItems(listItemModels, start, DateUtils.monthBetweenPure(tmp.getTime(), endPeriod.getTime()));

        listEventsAsyncProcessor.setEventsProcessorCallback(new DefaultEventsProcessorCallback(true));
        listEventsAsyncProcessor.queueEventsProcess(new Pair<>(start, endPeriod));
    }

    private void loadBefore() {
        Calendar tmp = (Calendar) startPeriod.clone();
        startPeriod.add(configuration.getPeriodType(), configuration.getPeriodValue() * -1);
        Calendar end = tmp;
        CalendarHelper.prepareListItems(listItemModels, startPeriod, DateUtils.monthBetweenPure(startPeriod.getTime(), end.getTime()));

        listEventsAsyncProcessor.setEventsProcessorCallback(new DefaultEventsProcessorCallback(true, true));
        listEventsAsyncProcessor.queueEventsProcess(new Pair<>(startPeriod, end));
    }

    private class DefaultEventsProcessorCallback implements EventsProcessorCallback<Pair<Calendar, Calendar>, List<Event>> {

        private boolean processCallback = false;
        private boolean keepPosition = false;

        DefaultEventsProcessorCallback(boolean processCallback) {
            this.processCallback = processCallback;
        }

        DefaultEventsProcessorCallback(boolean processCallback, boolean keepPosition) {
            this.processCallback = processCallback;
            this.keepPosition = keepPosition;
        }

        @Override
        public void onEventsProcessed(final Pair<Calendar, Calendar> target, final List<Event> result) {
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    final Set<ListItemModel> newItems = new TreeSet<>(getComparator());
                    newItems.addAll(listItemModels);
                    for (Event event : result) {
                        newItems.add(new ListItemModel(event.getEventStartDate(), event, ListItemModel.EVENT));
                    }

                    final Calendar previousDate = getCurrentDate();

                    listItemModels.clear();
                    listItemModels.addAll(newItems);

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();

                            if (keepPosition) setSelectedMonth(previousDate);

                            if (processCallback && callback != null)
                                callback.onEventsDisplayed(target);
                        }
                    });
                }
            });
        }
    }

    public void displayEvents() {
        displayEvents(new ArrayList<>(eventList), null);
    }

    public void displayEvents(List<Event> events, final DisplayEventCallback<Pair<Calendar, Calendar>> callback) {
        this.callback = callback;
        if (this.eventList == null) this.eventList = new ArrayList<>();

        eventList.clear();
        eventList.addAll(events);

        listEventsAsyncProcessor.setEvents(events);

        position = ((LinearLayoutManager) calendarListView.getLayoutManager()).findLastVisibleItemPosition();

        listEventsAsyncProcessor.setEventsProcessorCallback(new EventsProcessorCallback<Pair<Calendar, Calendar>, List<Event>>() {
            @Override
            public void onEventsProcessed(final Pair<Calendar, Calendar> target, final List<Event> result) {
                listEventsAsyncProcessor.setEvents(eventList);
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        final List<ListItemModel> freshListItemModels = new ArrayList<>();
                        freshListItemModels.addAll(listItemModels);

                        for (int i = freshListItemModels.size() - 1; i >= 0; i--) {
                            if (freshListItemModels.get(i).getType() == ListItemModel.EVENT) {
                                freshListItemModels.remove(i);
                            }
                        }

                        for (Event event : result) {
                            freshListItemModels.add(new ListItemModel(event.getEventStartDate(), event, ListItemModel.EVENT));
                        }
                        sortListItemsAscending(freshListItemModels);

                        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                            @Override
                            public int getOldListSize() {
                                return listItemModels.size();
                            }

                            @Override
                            public int getNewListSize() {
                                return freshListItemModels.size();
                            }

                            @Override
                            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                ListItemModel oldItem = listItemModels.get(oldItemPosition);
                                ListItemModel newItem = freshListItemModels.get(newItemPosition);

                                if (oldItem.getType() != newItem.getType()) {
                                    return false;

                                } else if ((oldItem.getType() == ListItemModel.WEEK && newItem.getType() == ListItemModel.WEEK)
                                        || (oldItem.getType() == ListItemModel.MONTH && newItem.getType() == ListItemModel.MONTH)) {
                                    return oldItem.getCalendar().getTimeInMillis() == newItem.getCalendar().getTimeInMillis();

                                } else {
                                    Event oldEvent = (Event) oldItem.getValue();
                                    Event newEvent = (Event) newItem.getValue();

                                    return oldEvent.getEventId().equals(newEvent.getEventId())
                                            && oldEvent.getEventStartDate().equals(newEvent.getEventStartDate());
                                }
                            }

                            @Override
                            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                ListItemModel oldItem = listItemModels.get(oldItemPosition);
                                ListItemModel newItem = freshListItemModels.get(newItemPosition);

                                if (oldItem.getType() == ListItemModel.EVENT && newItem.getType() == ListItemModel.EVENT) {
                                    Event oldEvent = (Event) oldItem.getValue();
                                    Event newEvent = (Event) newItem.getValue();

                                    return oldEvent.getEventTitle().equals(newEvent.getEventTitle())
                                            && oldEvent.getCalendarId().longValue() == newEvent.getCalendarId().longValue();
                                } else return true;
                            }
                        }, false);

                        listItemModels.clear();
                        listItemModels.addAll(freshListItemModels);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                diffResult.dispatchUpdatesTo(ListAdapter.this);
                                if (callback != null)
                                    callback.onEventsDisplayed(target);
                            }
                        });
                    }
                });
            }
        });
        listEventsAsyncProcessor.queueEventsProcess(new Pair<>(startPeriod, endPeriod));
    }

    public void addEvent(Event event) {
        if (this.eventList == null) this.eventList = new ArrayList<>();
        eventList.add(event);

        listEventsAsyncProcessor.setEvents(Collections.singletonList(event));
        listEventsAsyncProcessor.setEventsProcessorCallback(new DefaultEventsProcessorCallback(false));
        listEventsAsyncProcessor.queueEventsProcess(new Pair<>(startPeriod, endPeriod));
    }

    public void addEvents(List<Event> events) {
        if (this.eventList == null) this.eventList = new ArrayList<>();
        eventList.addAll(events);

        listEventsAsyncProcessor.setEvents(events);
        listEventsAsyncProcessor.setEventsProcessorCallback(new DefaultEventsProcessorCallback(false));
        listEventsAsyncProcessor.queueEventsProcess(new Pair<>(startPeriod, endPeriod));
    }

    public void editEvent(Event event) {
        removeEvent(event);
        event.setEventStartDate(event.getOriginalEventStartDate());
        addEvent(event);
    }

    public void removeEvent(Event event) {
        long id = event.getEventId();

        for (int i = 0; i < listItemModels.size(); ++i) {
            ListItemModel listItemModel = listItemModels.get(i);
            if (listItemModel.getType() == ListItemModel.EVENT) {
                long currentId = ((Event) listItemModel.getValue()).getEventId();
                if (id == currentId) {
                    listItemModels.remove(i);
                    notifyItemRemoved(i);
                    --i;
                }
            }
        }

        for (int i = 0; i < eventList.size(); ++i) {
            if (eventList.get(i).getEventId().equals(id)) {
                eventList.remove(i);
                --i;
            }
        }
    }

    public void setSelectedMonth(final Calendar selectedMonth) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                position = getDatePosition(selectedMonth);
                if (position + selectedMonth.getActualMaximum(Calendar.DAY_OF_MONTH) > listItemModels.size()) {
                    loadMoreEvents();
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPosition(position);
                    }
                });

            }
        });
    }

    private void scrollToPosition(int position) {
        ((LinearLayoutManager) calendarListView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
    }

    public void setSelectedDay(final Calendar selectedDay) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                position = getDatePosition(DateUtils.setTimeToMidnight(selectedDay));
                if (position + THRESHOLD >= listItemModels.size())
                    loadMoreEvents();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPosition(position);
                    }
                });
            }
        });
    }

    public void release() {
        listEventsAsyncProcessor.quit();
        listItemModels.clear();
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        ListHolder holder;
        if (viewType == ListItemModel.EVENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(configuration.getEventLayoutId(), parent, false);

            holder = new EventHolder(view, backgroundHandler, uiHandler, onEventClickListener, configuration.getEventDecoratorFactory());
            holder.setType(ListItemModel.EVENT);
        } else if (viewType == ListItemModel.WEEK) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(configuration.getWeekLayoutId(), parent, false);
            holder = new WeekHolder(view, backgroundHandler, uiHandler, configuration.getWeekDecoratorFactory());
            holder.setType(ListItemModel.WEEK);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(configuration.getMonthLayoutId(), parent, false);

            holder = new MonthHolder(calendarListView, view, backgroundHandler, uiHandler, configuration.getMonthDecoratorFactory());
            holder.setType(ListItemModel.MONTH);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        if (holder.getType() == ListItemModel.EVENT) {
            ((EventHolder) holder).bindView((Event) listItemModels.get(position).getValue(), position > 0 ? listItemModels.get(position - 1) : null, position);
        } else if (holder.getType() == ListItemModel.WEEK) {
            ((WeekHolder) holder).bindView((Pair<Calendar, Calendar>) listItemModels.get(position).getValue());
        } else if (holder.getType() == ListItemModel.MONTH) {
            ((MonthHolder) holder).bindView((Calendar) listItemModels.get(position).getValue());
        }
    }

    @Override
    public void onViewDetachedFromWindow(ListHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof MonthHolder) {
            ((MonthHolder) holder).detach();
        }
    }

    @Override
    public int getItemCount() {
        return listItemModels.size();
    }

    private Calendar getCurrentDate() {
        int position = getFirstVisibleItemPosition();
        return listItemModels.get(position > 0 ? position : 0).getCalendar();
    }

    private int getFirstVisibleItemPosition() {
        return ((LinearLayoutManager) calendarListView.getLayoutManager()).findFirstVisibleItemPosition();
    }

    private int getCurrentPosition() {
        int lastVisiblePosition = ((LinearLayoutManager) calendarListView.getLayoutManager()).findLastVisibleItemPosition();
        return getDatePosition(listItemModels.get(lastVisiblePosition).getCalendar());
    }

    public int getDatePosition(Calendar calendar) {
        long time = calendar.getTimeInMillis();
        long itemTime = 0;
        long nextItemTime = 0;
        int scrollPosition = 0;

        for (int i = 0; i < listItemModels.size(); ++i) {
            itemTime = listItemModels.get(i).getCalendar().getTimeInMillis();

            if (i + 1 < listItemModels.size())
                nextItemTime = listItemModels.get(i + 1).getCalendar().getTimeInMillis();
            else
                nextItemTime = listItemModels.get(listItemModels.size() - 1).getCalendar().getTimeInMillis();

            if (time >= itemTime && time < nextItemTime) {
                scrollPosition = i;
                break;
            }
        }

        if (scrollPosition == 0)
            scrollPosition = listItemModels.size() - 1;
        return scrollPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return listItemModels.get(position).getType();
    }

    @Override
    public void onSetLayoutManager(RecyclerView.LayoutManager layout) {
        calendarListView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) layout, THRESHOLD) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                calendarListView.stopScroll();
                loadMoreEvents();
            }
        });
        calendarListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private Calendar savedDate;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //scrolled by scroll to position
                if (dy == 0) {
                    return;
                }

                if (!calendarListView.canScrollVertically(-1)) {
                    loadBefore();
                }

                if (getFirstVisibleItemPosition() > listItemModels.size())
                    return;

                final ListItemModel firstVisibleItem = listItemModels.get(getFirstVisibleItemPosition());
                if (savedDate == null) {
                    savedDate = firstVisibleItem.getCalendar();
                    return;
                }
                if (dy > 0) {
                    //month changed
                    if (firstVisibleItem.getType() == ListItemModel.MONTH && !firstVisibleItem.getCalendar().equals(savedDate)) {
                        savedDate = firstVisibleItem.getCalendar();
                        if (monthChangeListener != null)
                            monthChangeListener.onMonthChanged(savedDate);
                    }
                } else {
                    //month changed
                    if (savedDate != null && firstVisibleItem.getCalendar().get(Calendar.MONTH) != savedDate.get(Calendar.MONTH)) {
                        savedDate = firstVisibleItem.getCalendar();
                        if (monthChangeListener != null)
                            monthChangeListener.onMonthChanged(savedDate);
                    }
                }

                //day changed
                if (firstVisibleItem.getType() == ListItemModel.EVENT && firstVisibleItem.getCalendar().get(Calendar.DATE) != savedDate.get(Calendar.DATE)) {
                    savedDate = firstVisibleItem.getCalendar();
                    if (dayChangeListener != null)
                        dayChangeListener.onDayChanged(savedDate);
                }
            }
        });
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    private void sortListItemsAscending(List<ListItemModel> eventList) {
        Collections.sort(eventList, getComparator());
    }

    private Comparator<ListItemModel> getComparator() {
        return new Comparator<ListItemModel>() {
            @Override
            public int compare(ListItemModel lhs, ListItemModel rhs) {
                return lhs.compareTo(rhs);
            }
        };
    }
}
