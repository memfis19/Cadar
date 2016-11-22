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
import io.github.memfis19.cadar.data.process.IEventProcessor;
import io.github.memfis19.cadar.internal.process.utils.CircularFifoQueue;
import io.github.memfis19.cadar.internal.helper.ScrollManager;

/**
 * Created by memfis on 7/22/16.
 */
public abstract class BaseEventsAsyncProcessor<T, E> extends HandlerThread implements ScrollManager.OnScrollChanged {

    private static final String TAG = "BaseEventsAsyncProcessor";

    private static final int MESSAGE_EVENTS_PROCESS = 126;

    private IEventProcessor iEventProcessor;
    private List<Event> eventList;
    private Queue<Pair<T, E>> resultQueue = new CircularFifoQueue<>(3);

    private boolean shouldProcess;

    private boolean hasQuit = false;

    private Handler requestHandler;
    private Handler responseHandler = new Handler(Looper.getMainLooper());

    private EventsProcessorListener eventsProcessorListener;

    public interface EventsProcessorListener<T, E> {
        void onEventsProcessed(T target, E result);
    }

    public BaseEventsAsyncProcessor(boolean shouldProcess, IEventProcessor eventProcessor) {
        super(String.valueOf(System.currentTimeMillis()), Process.THREAD_PRIORITY_BACKGROUND);

        this.shouldProcess = shouldProcess;
        this.iEventProcessor = eventProcessor;

        ScrollManager.getInstance().subscribeForScrollStateChanged(this);
    }

    protected IEventProcessor getEventProcessor() {
        return iEventProcessor;
    }

    protected List<Event> getEventList() {
        return eventList;
    }

    protected boolean isShouldProcess() {
        return shouldProcess;
    }

    public void setEventProcessor(IEventProcessor eventProcessor) {
        this.iEventProcessor = eventProcessor;
    }

    public void setEvents(List<Event> eventList) {
        this.eventList = eventList;
        this.iEventProcessor.setEventsToProcess(eventList);
    }

    public void setEventsProcessorListener(EventsProcessorListener<T, E> eventsProcessorListener) {
        this.eventsProcessorListener = eventsProcessorListener;
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
        deliverResult(target, processEvents(target));
    }

    protected abstract E processEvents(final T target);

    private void deliverResult(final T target, final E result) {
        if (ScrollManager.getInstance().getCurrentScrollState() == ScrollManager.SCROLL_STATE_IDLE) {
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (hasQuit) return;

                    if (eventsProcessorListener != null)
                        eventsProcessorListener.onEventsProcessed(target, result);

                }
            });
        } else {
            resultQueue.add(new Pair(target, result));
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

                        if (eventsProcessorListener != null)
                            eventsProcessorListener.onEventsProcessed(pair.first, pair.second);

                        resultQueue.remove(pair);
                    }
                });
            }
        }
    }

}
