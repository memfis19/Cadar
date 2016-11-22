package io.github.memfis19.cadar.internal.ui.list.adapter.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

import io.github.memfis19.cadar.internal.utils.DateUtils;

/**
 * Created by memfis on 9/5/16.
 */
public class ListItemModel implements Comparable<ListItemModel> {

    public static final int MONTH = 460;
    public static final int WEEK = 430;
    public static final int EVENT = 610;

    @IntDef({MONTH, WEEK, EVENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType {
    }

    private Object value;

    @ItemType
    private int type;

    private Calendar calendar;

    public ListItemModel(Calendar calendar, Object value, @ItemType int type) {
        this.value = value;
        this.type = type;
        this.calendar = calendar;
    }

    public ListItemModel(Date date, Object value, @ItemType int type) {
        this.value = value;
        this.type = type;

        calendar = DateUtils.getCalendarInstance();
        calendar.setTime(date);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @ItemType
    public int getType() {
        return type;
    }

    public void setType(@ItemType int type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull ListItemModel listItemModel) {
        Long currentValue = getCalendar().getTimeInMillis();
        Long passedValue = listItemModel.getCalendar().getTimeInMillis();

        int result = currentValue.compareTo(passedValue);
        if (result == 0) {
            if (getType() == listItemModel.getType()) return 0;

            if (getType() == EVENT) return 1;
            else if (listItemModel.getType() == EVENT) return -1;

            if (getType() == WEEK && listItemModel.getType() == MONTH) return 1;
            else return -1;
        }
        return result;
    }
}
