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

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.process.impl.Ical4jEventProcessor;
import io.github.memfis19.cadar.event.DisplayEventCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.process.BaseEventsAsyncProcessor;
import io.github.memfis19.cadar.internal.process.ListEventsAsyncProcessor;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.EventItemHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.ListItemHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.holder.WeekTitleHolder;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;
import io.github.memfis19.cadar.internal.ui.list.event.EndlessRecyclerViewScrollListener;
import io.github.memfis19.cadar.internal.utils.CalendarHelper;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.view.ListCalendar;

/**
 * Created by memfis on 9/5/16.
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ListCalendar.OnSetLayoutManagerListener {

    private static final String TAG = "ListAdapter";
    private static final int MONTH_IN_ONE_YEAR = 12;

    private static final int THRESHOLD = 5;

    private RecyclerView recyclerView;
    private List<ListItemModel> listItemModels;

    private Handler backgroundHandler;
    private Handler uiHandler;

    private ListEventsAsyncProcessor listEventsAsyncProcessor;
    private int position;

    private Calendar startPeriod;
    private Calendar endPeriod;
    private ListCalendarConfiguration configuration;

    private int countOfLoadedYears = 0;

    private OnMonthChangeListener monthChangeListener;
    private OnDayChangeListener dayChangeListener;
    private OnEventClickListener onEventClickListener;

    private List<Event> eventList = new ArrayList<>();

    public ListAdapter(ListCalendarConfiguration configuration,
                       RecyclerView recyclerView,
                       List<ListItemModel> listItemModels,
                       List<Event> eventList,
                       Calendar startPeriod, Calendar endPeriod,
                       Handler backgroundHandler, Handler uiHandler,
                       OnMonthChangeListener monthChangeLister,
                       OnDayChangeListener dayChangeListener) {

        this.recyclerView = recyclerView;
        this.listItemModels = listItemModels;
        this.backgroundHandler = backgroundHandler;
        this.uiHandler = uiHandler;

        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.configuration = configuration;
        this.eventList = eventList;

        this.monthChangeListener = monthChangeLister;
        this.dayChangeListener = dayChangeListener;

        listEventsAsyncProcessor = new ListEventsAsyncProcessor(configuration.isEventProcessingEnabled(), configuration.getEventProcessor());
        listEventsAsyncProcessor.setEventProcessor(new Ical4jEventProcessor());
        listEventsAsyncProcessor.setEvents(eventList);
        listEventsAsyncProcessor.start();
        listEventsAsyncProcessor.getLooper();

        countOfLoadedYears = configuration.getCapacityYears();
    }

    private void loadMoreEvents() {
        if (countOfLoadedYears <= configuration.getMaxYearsToDisplay()) {
            endPeriod.add(Calendar.YEAR, 1);
            Calendar start = DateUtils.setTimeToYearStart((Calendar) endPeriod.clone());
            CalendarHelper.prepareListItems(listItemModels, start, MONTH_IN_ONE_YEAR);
            listEventsAsyncProcessor.setEventsProcessorListener(new BaseEventsAsyncProcessor.EventsProcessorListener<Pair<Calendar, Calendar>, List<Event>>() {
                @Override
                public void onEventsProcessed(Pair<Calendar, Calendar> target, final List<Event> result) {
                    backgroundHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            List<ListItemModel> newItems = new ArrayList<>();
                            newItems.addAll(listItemModels);
                            for (Event event : result) {
                                newItems.add(new ListItemModel(event.getEventStartDate(), event, ListItemModel.EVENT));
                            }
                            sortListItemsAscending(newItems);

                            listItemModels.clear();
                            listItemModels.addAll(newItems);

                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            });
            listEventsAsyncProcessor.queueEventsProcess(new Pair<>(start, endPeriod));
            countOfLoadedYears++;
        }
    }

    public void displayEvents(List<Event> events, final DisplayEventCallback callback) {
        if (this.eventList == null) this.eventList = new ArrayList<>();

        eventList.clear();
        eventList.addAll(events);

        listEventsAsyncProcessor.setEvents(events);

        startPeriod.setTime(DateUtils.setTimeToYearStart(startPeriod.getTime()));
        endPeriod.setTime(DateUtils.setTimeToYearEnd(endPeriod.getTime()));

        position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        listEventsAsyncProcessor.setEventsProcessorListener(new BaseEventsAsyncProcessor.EventsProcessorListener<Pair<Calendar, Calendar>, List<Event>>() {
            @Override
            public void onEventsProcessed(Pair<Calendar, Calendar> target, final List<Event> result) {
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
                                    callback.onEventsDisplayed();
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
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder holder;
        if (viewType == ListItemModel.EVENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_layout, parent, false);
            holder = new EventItemHolder(view, backgroundHandler, uiHandler, onEventClickListener);
        } else if (viewType == ListItemModel.WEEK) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.week_title_layout, parent, false);
            holder = new WeekTitleHolder(view, backgroundHandler, uiHandler);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_calendar_item_layout, parent, false);
            holder = new ListItemHolder(view, backgroundHandler, uiHandler);
            recyclerView.addOnScrollListener(((ListItemHolder) holder).getScrollListener());
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ListItemModel.EVENT) {
            ((EventItemHolder) holder).bindView((Event) listItemModels.get(position).getValue(), position > 0 ? listItemModels.get(position - 1) : null, position);
        } else if (getItemViewType(position) == ListItemModel.WEEK) {
            ((WeekTitleHolder) holder).bindView((Pair<Calendar, Calendar>) listItemModels.get(position).getValue());
        } else if (getItemViewType(position) == ListItemModel.MONTH) {
            ((ListItemHolder) holder).bindView((Calendar) listItemModels.get(position).getValue());
        }
    }

    @Override
    public int getItemCount() {
        return listItemModels.size();
    }

    private Calendar getCurrentDate() {
        return listItemModels.get(getFirstVisibleItemPosition()).getCalendar();
    }

    private int getFirstVisibleItemPosition() {
        return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
    }

    private int getCurrentPosition() {
        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        return getDatePosition(listItemModels.get(lastVisiblePosition).getCalendar());
    }

    public int getDatePosition(Calendar calendar) {
        long time = calendar.getTimeInMillis();
        long itemTime = 0;
        int scrollPosition = 0;
        for (int i = 0; i < listItemModels.size(); ++i) {
            itemTime = listItemModels.get(i).getCalendar().getTimeInMillis();
            if (time <= itemTime) {
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
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) layout, THRESHOLD) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                recyclerView.stopScroll();
                loadMoreEvents();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
        Collections.sort(eventList, new Comparator<ListItemModel>() {
            @Override
            public int compare(ListItemModel lhs, ListItemModel rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }
}
