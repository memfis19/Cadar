package io.github.memfis19.cadar.internal.ui.list.adapter.decorator.factory;

import android.view.View;

import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.WeekDecorator;

/**
 * Created by memfis on 12/15/16.
 */

public interface WeekDecoratorFactory {

    WeekDecorator createWeekDecorator(View parent);

}
