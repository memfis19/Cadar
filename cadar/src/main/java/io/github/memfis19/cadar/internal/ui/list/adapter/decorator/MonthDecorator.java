package io.github.memfis19.cadar.internal.ui.list.adapter.decorator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Calendar;

/**
 * Created by memfis on 11/24/16.
 */

public interface MonthDecorator {
    void onBindMonthView(View view, Calendar month);

    @NonNull
    RecyclerView.OnScrollListener getScrollListener();
}
