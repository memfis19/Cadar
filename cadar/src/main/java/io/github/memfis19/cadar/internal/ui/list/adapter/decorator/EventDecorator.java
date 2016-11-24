package io.github.memfis19.cadar.internal.ui.list.adapter.decorator;

import android.view.View;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;

/**
 * Created by memfis on 11/24/16.
 */

public interface EventDecorator {
    void onBindEventView(View view, Event event, ListItemModel previous, int position);
}
