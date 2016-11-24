package io.github.memfis19.cadar.internal.ui.month.adapter.holder;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.internal.ui.month.MonthCalendarHelper;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.MonthDayDecorator;

/**
 * Created by memfis on 7/13/16.
 */
public class MonthDayHolder extends RecyclerView.ViewHolder {

    private View itemView;

    private TextView dayNumberView;
    private OnDayChangeListener onDateChangeListener;
    private Calendar day;

    public MonthDayHolder(View itemView, final OnDayChangeListener onDateChangeListener) {
        super(itemView);
        this.itemView = itemView;
        this.onDateChangeListener = onDateChangeListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDateChangeListener != null) {
                    MonthCalendarHelper.updateSelectedDay(day);
                    onDateChangeListener.onDayChanged(day);
                }
            }
        });
    }

    public void bindView(final Calendar calendar,
                         @Nullable List<Event> eventList,
                         boolean isSelected,
                         boolean isToday,
                         @Nullable MonthDayDecorator monthDayDecorator) {

        day = calendar;

        if (monthDayDecorator != null) {
            monthDayDecorator.onBindDayView(itemView, calendar, eventList, isSelected, isToday);
        } else {
            dayNumberView = (TextView) itemView.findViewById(R.id.month_view_item_content);
            dayNumberView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            if (isSelected) {
                dayNumberView.setBackgroundResource(R.drawable.event_selected_background);
            } else if (isToday) {
                dayNumberView.setBackgroundResource(R.drawable.event_today_background);
            } else {
                dayNumberView.setBackgroundColor(Color.TRANSPARENT);
            }

            if (eventList == null || eventList.isEmpty()) {
                dayNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                dayNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.event_indicator);
            }
        }
    }
}
