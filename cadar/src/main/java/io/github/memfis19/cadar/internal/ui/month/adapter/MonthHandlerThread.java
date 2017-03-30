package io.github.memfis19.cadar.internal.ui.month.adapter;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by memfis on 7/25/16.
 * Helper class to fill the month adapter asynchronously
 */
public class MonthHandlerThread extends HandlerThread {

    private static final String TAG = "MonthHandlerThread";

    private static final int REQUEST_MONTH_PREPARATION = 852;

    private static final int NUM_OF_DAYS_IN_WEEK = 7;

    private class MonthInfoDto {
        private Calendar calendar;
        private Integer shiftValue;
        private RecyclerView recyclerView;

        MonthInfoDto(Calendar calendar, Integer shiftValue, RecyclerView recyclerView) {
            this.calendar = calendar;
            this.shiftValue = shiftValue;
            this.recyclerView = recyclerView;
        }
    }

    interface AdapterPrepareListener {
        void onReadyAdapter(Calendar month, List<Calendar> monthDays, RecyclerView recyclerView);
    }

    private boolean hasQuit = false;

    private Handler requestHandler;
    private Handler responseHandler;
    private AdapterPrepareListener adapterPrepareListener;

    public MonthHandlerThread() {
        super(TAG, Process.THREAD_PRIORITY_BACKGROUND);
    }

    void setAdapterPrepareListener(AdapterPrepareListener adapterPrepareListener) {
        this.adapterPrepareListener = adapterPrepareListener;
    }

    @Override
    protected void onLooperPrepared() {
        responseHandler = new Handler(Looper.getMainLooper());

        requestHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_MONTH_PREPARATION) {
                    handlePreparation((MonthInfoDto) msg.obj);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        hasQuit = true;
        return super.quit();
    }

    @Override
    public boolean quitSafely() {
        hasQuit = true;
        return (Build.VERSION.SDK_INT > 17) ? super.quitSafely() : super.quit();
    }

    void queuePreparation(Calendar calendar, Integer shiftValue, RecyclerView recyclerView) {
        if (hasQuit || requestHandler == null) return;
        requestHandler.obtainMessage(REQUEST_MONTH_PREPARATION,
                new MonthInfoDto(calendar, shiftValue, recyclerView)).sendToTarget();
    }

    private void handlePreparation(final MonthInfoDto monthInfoDto) {
        final Calendar month = (Calendar) monthInfoDto.calendar.clone();
        month.add(Calendar.MONTH, monthInfoDto.shiftValue);

        month.set(Calendar.DAY_OF_MONTH, 1);
        final List<Calendar> monthDays = prepare(month);

        if (!hasQuit && adapterPrepareListener != null)
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapterPrepareListener.onReadyAdapter(month, monthDays, monthInfoDto.recyclerView);
                }
            });
    }

    private List<Calendar> prepare(Calendar monthCalendar) {

        List<Calendar> monthDays = new ArrayList<>();
        Calendar iterator = (Calendar) monthCalendar.clone();

        int month = iterator.get(Calendar.MONTH);
        int lastDay = iterator.getActualMaximum(Calendar.DATE);

        iterator.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (iterator.get(Calendar.MONTH) == month && iterator.get(Calendar.DATE) > 1) {
            iterator.add(Calendar.DATE, -7);
        }

        boolean finish = false;
        while (!finish) {

            for (int i = 0; i < NUM_OF_DAYS_IN_WEEK; i++) {
                if (month == iterator.get(Calendar.MONTH) && lastDay == iterator.get(Calendar.DATE)) {
                    finish = true;
                }
                monthDays.add((Calendar) iterator.clone());
                iterator.add(Calendar.DATE, 1);
            }
        }

        return monthDays;
    }

}
