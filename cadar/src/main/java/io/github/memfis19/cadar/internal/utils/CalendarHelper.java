package io.github.memfis19.cadar.internal.utils;

import android.support.v4.util.Pair;

import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;


/**
 * Created by memfis on 9/9/16.
 */
public final class CalendarHelper {

    private CalendarHelper() {
    }

    public static void prepareListItems(List<ListItemModel> listItemModels, Calendar startPeriod, int capacityMonth) {
        for (int i = 0; i < capacityMonth; ++i) {
            Calendar temp = (Calendar) startPeriod.clone();
            temp.add(Calendar.MONTH, i);
            listItemModels.add(new ListItemModel(temp, temp, ListItemModel.MONTH));
            CalendarHelper.prepareWeekModel((Calendar) temp.clone(), listItemModels);
        }
    }

    public static void prepareWeekModel(Calendar month, List<ListItemModel> listItemModels) {
        int monthValue = month.get(Calendar.MONTH);
        month.set(Calendar.DAY_OF_MONTH, 1);

        while (month.get(Calendar.DAY_OF_WEEK) != month.getFirstDayOfWeek()) {
            month.add(Calendar.DAY_OF_MONTH, 1);
        }
        int startWeek = month.get(Calendar.WEEK_OF_YEAR);
        while (month.get(Calendar.MONTH) == monthValue) {
            Calendar start = DateUtils.setTimeToMidnight((Calendar) month.clone());
            month.add(Calendar.DAY_OF_YEAR, 6);
            Calendar end = DateUtils.setTimeToEndOfTheDay((Calendar) month.clone());

            listItemModels.add(new ListItemModel(start, new Pair<>(start, end), ListItemModel.WEEK));

            month.add(Calendar.DAY_OF_YEAR, -6);
            month.add(Calendar.WEEK_OF_YEAR, 1);
        }
        int endWeek = month.get(Calendar.WEEK_OF_YEAR);

        int weeksAmount = endWeek - startWeek;
        month.set(Calendar.WEEK_OF_YEAR, month.get(Calendar.WEEK_OF_YEAR) - weeksAmount);
    }

}
