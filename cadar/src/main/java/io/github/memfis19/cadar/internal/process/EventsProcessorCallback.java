package io.github.memfis19.cadar.internal.process;

/**
 * Created by memfis on 3/28/17.
 */
public interface EventsProcessorCallback<T, E> {
    void onEventsProcessed(T target, E result);
}