package io.github.memfis19.cadar.data.factory;

import io.github.memfis19.cadar.data.entity.Event;

/**
 * Created by memfis on 7/21/16.
 */
public interface EventFactory {
    Event createEventCopy(Event event);
}
