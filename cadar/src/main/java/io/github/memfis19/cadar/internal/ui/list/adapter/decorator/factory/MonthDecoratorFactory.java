package io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory;

import android.view.View;

import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.MonthDecorator;

/**
 * Created by memfis on 12/15/16.
 */

public interface MonthDecoratorFactory {

    MonthDecorator createMonthDecorator(View parent);

}
