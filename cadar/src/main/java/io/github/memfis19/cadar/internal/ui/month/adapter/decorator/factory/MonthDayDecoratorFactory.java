package io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory;

import android.view.View;

import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.MonthDayDecorator;

/**
 * Created by memfis on 12/15/16.
 */

public interface MonthDayDecoratorFactory {

    MonthDayDecorator createMonthDayDecorator(View parent);

}
