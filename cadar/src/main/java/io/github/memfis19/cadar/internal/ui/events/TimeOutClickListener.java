package io.github.memfis19.cadar.internal.ui.events;

import android.view.View;

/**
 * Created by serg on 27.11.15.
 */
public abstract class TimeOutClickListener implements View.OnClickListener {

    private static boolean isClicked = false;
    private long timeout = 500;

    public TimeOutClickListener() {
        super();
    }

    @Override
    public void onClick(View view) {
        if (!isClicked && view != null) {
            isClicked = true;

            onViewClick(view);

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClicked = false;
                }
            }, timeout);
        }
    }

    public abstract void onViewClick(View view);
}