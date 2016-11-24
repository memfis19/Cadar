package io.github.memfis19.cadar.internal.ui.list.adapter.decorator;

import android.support.v4.util.Pair;
import android.view.View;

import java.util.Calendar;

/**
 * Created by memfis on 11/24/16.
 */

public interface WeekDecorator {
    void onBindWeekView(View view, Pair<Calendar, Calendar> period);
}
