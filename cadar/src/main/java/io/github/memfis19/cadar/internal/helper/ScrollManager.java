package io.github.memfis19.cadar.internal.helper;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by memfis on 7/13/16.
 */
public final class ScrollManager {

    static private Map<View, ScrollManager> viewScrollManagerMap = new HashMap<>();

    public static final int SCROLL_STATE_IDLE = 1;
    public static final int SCROLL_STATE_DRAGGING = 2;
    public static final int SCROLL_STATE_SETTLING = 3;

    @IntDef({SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollState {
    }

    public interface OnScrollChanged {
        void onScrollStateChanged(@ScrollState int scrollState);
    }

    private View view;
    private int currentScrollState = SCROLL_STATE_IDLE;
    private List<WeakReference<OnScrollChanged>> subscribers = new ArrayList<>();

    public static ScrollManager geViewInstance(View view) {
        if (viewScrollManagerMap.get(view) == null) {
            viewScrollManagerMap.put(view, new ScrollManager(view));
        }
        return viewScrollManagerMap.get(view);
    }

    private ScrollManager(View view) {
        this.view = view;
    }

    public void notifyScrollStateChanged(@ScrollState int scrollState) {
        if (currentScrollState != scrollState) {
            currentScrollState = scrollState;
            notifyAllSubscribers();
        }
    }

    public int getCurrentScrollState() {
        return currentScrollState;
    }

    public void subscribeForScrollStateChanged(OnScrollChanged onScrollChanged) {
        subscribers.add(new WeakReference<>(onScrollChanged));
    }

    public void unSubscribeForScrollStateChanged(@NonNull OnScrollChanged onScrollChanged) {
        for (WeakReference<OnScrollChanged> weakReference : subscribers) {
            if (onScrollChanged.equals(weakReference.get())) {
                subscribers.remove(weakReference);
                return;
            }
        }
    }

    private void notifyAllSubscribers() {
        for (WeakReference<OnScrollChanged> weakReference : subscribers) {
            if (weakReference.get() != null)
                weakReference.get().onScrollStateChanged(currentScrollState);
            else subscribers.remove(weakReference);
        }
    }

}
