package io.github.memfis19.cadar.internal.process;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.util.Pair;

import java.util.List;
import java.util.Queue;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.process.EventCalculator;
import io.github.memfis19.cadar.internal.helper.ScrollManager;
import io.github.memfis19.cadar.internal.process.utils.CircularFifoQueue;

/**
 * Created by memfis on 7/22/16.
 */
public abstract class EventsProcessor<T, E> extends HandlerThread implements ScrollManager.OnScrollChanged {

    private static final String TAG = "EventsProcessor";

    private static final int MESSAGE_EVENTS_PROCESS = 126;

    private EventCalculator eventCalculator;
    private List<Event> eventList;
    private Queue<Pair<T, E>> resultQueue = new CircularFifoQueue<>(3);

    private boolean shouldProcess;
    private boolean processAsync = false;
    private boolean hasQuit = false;

    private Handler requestHandler;
    private Handler responseHandler = new Handler(Looper.getMainLooper());

    private EventsProcessorCallback<T, E> eventsProcessorCallback;

    public EventsProcessor(boolean shouldProcess, EventCalculator eventProcessor, boolean processAsync) {
        super(String.valueOf(System.currentTimeMillis()), Process.THREAD_PRIORITY_BACKGROUND);

        this.shouldProcess = shouldProcess;
        this.eventCalculator = eventProcessor;
        this.processAsync = processAsync;

        ScrollManager.getInstance().subscribeForScrollStateChanged(this);
    }

    protected EventCalculator getEventProcessor() {
        return eventCalculator;
    }

    protected List<Event> getEventList() {
        return eventList;
    }

    protected boolean isShouldProcess() {
        return shouldProcess;
    }

    public void setEventProcessor(EventCalculator eventProcessor) {
        this.eventCalculator = eventProcessor;
    }

    public void setEvents(List<Event> eventList) {
        this.eventList = eventList;
        this.eventCalculator.setEventsToProcess(eventList);
    }

    public void setEventsProcessorCallback(EventsProcessorCallback<T, E> eventsProcessorCallback) {
        this.eventsProcessorCallback = eventsProcessorCallback;
    }

    public void queueEventsProcess(T target) {
        if (target == null) return;
        requestHandler.obtainMessage(MESSAGE_EVENTS_PROCESS, target).sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        requestHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == MESSAGE_EVENTS_PROCESS) {
                    T target = (T) message.obj;
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        release();
        return super.quit();
    }

    @Override
    public boolean quitSafely() {
        release();
        return (Build.VERSION.SDK_INT > 17) ? super.quitSafely() : super.quit();
    }

    private void release() {
        hasQuit = true;
        resultQueue.clear();
        ScrollManager.getInstance().unSubscribeForScrollStateChanged(this);
    }

    private void handleRequest(final T target) {
        if (processAsync) processEventsAsync(target, eventsProcessorCallback);
        else deliverResult(target, processEvents(target));
    }

    protected E processEvents(final T target) {
        return null;
    }

    protected void processEventsAsync(final T target, EventsProcessorCallback<T, E> eventsProcessorCallback) {
    }

    private void deliverResult(final T target, final E result) {
        if (ScrollManager.getInstance().getCurrentScrollState() == ScrollManager.SCROLL_STATE_IDLE) {
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (hasQuit) return;

                    if (eventsProcessorCallback != null)
                        eventsProcessorCallback.onEventsProcessed(target, result);

                }
            });
        } else {
            resultQueue.add(new Pair<>(target, result));
        }
    }

    @Override
    public void onScrollStateChanged(@ScrollManager.ScrollState int scrollState) {
        if (scrollState == ScrollManager.SCROLL_STATE_IDLE) {
            for (final Pair<T, E> pair : resultQueue) {
                responseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (hasQuit) return;

                        if (eventsProcessorCallback != null)
                            eventsProcessorCallback.onEventsProcessed(pair.first, pair.second);

                        resultQueue.remove(pair);
                    }
                });
            }
        }
    }

}
