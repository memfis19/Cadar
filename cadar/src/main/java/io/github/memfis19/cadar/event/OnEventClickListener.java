package io.github.memfis19.cadar.event;

import io.github.memfis19.cadar.data.entity.Event;

public interface OnEventClickListener {

    void onEventClick(Event event, int position);

    void onEventLongClick(Event event, int position);

    void onSyncClick(Event event, int position);

}
