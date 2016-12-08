package io.github.memfis19.cadar.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.OnDayChangeListener;
import io.github.memfis19.cadar.event.OnMonthChangeListener;
import io.github.memfis19.cadar.internal.ui.events.TimeOutClickListener;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.internal.utils.ViewUtils;
import io.github.memfis19.cadar.internal.utils.WordUtils;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;

/**
 * Created by serg on 19.10.15.
 */
public class ExtendedMonthCalendar extends RelativeLayout {

    public static final int STATE_NOT_READY = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_PREPARE = 2;

    private int currentCalendarState = STATE_NOT_READY;

    private static final String DAY_FORMAT = "d";
    private static final String MONTH_FORMAT = "MMMM";
    private static final String YEAR_FORMAT = "yyyy";
    private static final String THREAD_NAME = "StpMonthCalendarExtended2.process_text_thread";

    private final static int BORDER_WIDTH = 1;

    private Context context;
    private int colorTheme;

    private int startOfWeekDay;
    private Calendar selectedDay;
    private Calendar pointingDay;
    private Calendar currentMonth;

    private List<Event> eventList = new ArrayList<>();

    private Drawable arrowUpDrawable;
    private Drawable arrowDownDrawable;

    private TextView monthYearLabel;
    private MonthCalendar monthView;
    private TextView todayTextView;
    private View bottomLine;

    private boolean isCalendarCollapsed = false;
    private boolean isCanCollapse = true;
    private MonthCalendarExtendedListener listener;

    private Handler uiHandler = new Handler();
    private TextProcessor handlerThread;
    private TextTask textProcessingTask;

    @IntDef({STATE_NOT_READY, STATE_PREPARE, STATE_READY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface States {
    }

    public interface MonthCalendarExtendedListener {
        void onCollapse();

        void onExpand();

        void onDaySelected(Calendar selectedDay);

        void onMonthChanged(Calendar calendar);
    }

    public ExtendedMonthCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setClickable(true);
        initView();
    }

    public ExtendedMonthCalendar(Context context) {
        this(context, null);
    }

    public void setup(MonthCalendarConfiguration monthCalendarConfiguration, Calendar pointingDay,
                      int startOfWeekDay, List<Event> events, int colorTheme) {

        this.pointingDay = pointingDay;
        if (selectedDay == null)
            this.selectedDay = DateUtils.getCalendarInstance();
        this.currentMonth = DateUtils.getCalendarInstance();

        this.startOfWeekDay = startOfWeekDay;
        this.eventList = events;
        this.colorTheme = colorTheme;

        handlerThread = new TextProcessor(THREAD_NAME);
        handlerThread.start();
        handlerThread.prepareHandler();
        textProcessingTask = new TextTask();

        init(monthCalendarConfiguration);
    }

    public void update(List<Event> events) {
        this.eventList = events;
        monthView.displayEvents(eventList, null);
    }

    public void release() {
        monthView.releaseCalendar();
        handlerThread.quit();
    }

    public void isCanCollapse(boolean isCanCollapse) {
        this.isCanCollapse = isCanCollapse;
    }

    private void initView() {
        inflate(context, R.layout.extended_month_calendar_layout, this);
        todayTextView = (TextView) findViewById(R.id.today_day_label);
        findViewById(R.id.today_day_label_container).setOnClickListener(new TimeOutClickListener() {
            @Override
            public void onViewClick(View view) {
                selectedDay = DateUtils.getCalendarInstance();
                monthView.setSelectedDay(selectedDay, true);
                updateMonthAndYearTitle(selectedDay);
                if (listener != null)
                    listener.onDaySelected(selectedDay);

            }
        });

        monthYearLabel = (TextView) findViewById(R.id.month_year_label);
        monthView = (MonthCalendar) findViewById(R.id.month_calendar);
        bottomLine = findViewById(R.id.monthViewBottomLine);
    }

    public
    @States
    int getCurrentCalendarState() {
        return currentCalendarState;
    }

    private void init(MonthCalendarConfiguration monthCalendarConfiguration) {

        monthView.prepareCalendar(monthCalendarConfiguration);
        currentCalendarState = STATE_PREPARE;

        todayTextView.setText(DateUtils.dateToString(pointingDay.getTime(), DAY_FORMAT));
        Drawable drawable = todayTextView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setStroke(ViewUtils.convertDipToPixels(BORDER_WIDTH, context), colorTheme);
        }

        ArrayAdapter<CharSequence> viewMenuAdapter = ArrayAdapter
                .createFromResource(context, R.array.calendar_view_options,
                        R.layout.extended_month_calendar_view_option_spinner_item);
        viewMenuAdapter.setDropDownViewResource(R.layout.extended_month_calendar_view_option_spinner_dropdown_item);

        arrowUpDrawable = ViewUtils.getDrawable(context, R.drawable.ic_arrow_drop_up_white_24dp);
        arrowUpDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.month_title_color), PorterDuff.Mode.MULTIPLY);
        arrowUpDrawable.setBounds(0, 0, arrowUpDrawable.getIntrinsicWidth(), arrowUpDrawable.getIntrinsicHeight());

        arrowDownDrawable = ViewUtils.getDrawable(context, R.drawable.ic_arrow_drop_down_white_24dp);
        arrowDownDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.month_title_color), PorterDuff.Mode.MULTIPLY);
        arrowDownDrawable.setBounds(0, 0, arrowDownDrawable.getIntrinsicWidth(), arrowDownDrawable.getIntrinsicHeight());

        updateMonthAndYearTitle(selectedDay);

        monthYearLabel.setOnClickListener(new TimeOutClickListener() {
            @Override
            public void onViewClick(View view) {
                if (!isCanCollapse) return;
                if (isCalendarCollapsed) {
                    expand();
                } else {
                    collapse();
                }
            }
        });

        if (isCanCollapse) {
            if (isCalendarCollapsed) collapse();
            else expand();
        } else {
            monthYearLabel.setCompoundDrawables(null, null, null, null);
            bottomLine.setVisibility(INVISIBLE);
        }

        monthView.setCalendarPrepareCallback(new CalendarPrepareCallback() {
            @Override
            public void onCalendarReady(CalendarController calendar) {
                currentCalendarState = STATE_READY;

                monthView.setSelectedDay(selectedDay, true);

                monthView.setOnDayChangeListener(new OnDayChangeListener() {
                    @Override
                    public void onDayChanged(Calendar calendar) {
                        if (currentMonth.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                            currentMonth = calendar;
                            monthView.setSelectedDay(calendar, true);
                            updateMonthAndYearTitle(calendar);
                        }
                        selectedDay = calendar;
                        if (listener != null)
                            listener.onDaySelected(selectedDay);

                    }
                });

                monthView.setOnMonthChangeListener(new OnMonthChangeListener() {
                    @Override
                    public void onMonthChanged(Calendar calendar) {
                        currentMonth = calendar;
                        updateMonthAndYearTitle(calendar);
                        if (listener != null)
                            listener.onMonthChanged(calendar);
                    }
                });
            }
        });
    }

    private void updateMonthAndYearTitle(final Calendar calendar) {
        textProcessingTask.setDay(calendar);
        handlerThread.postTask(textProcessingTask);
    }

    public void setInitialDay(Calendar selectedDay) {
        this.selectedDay = selectedDay;
    }

    public void setSelectedDay(Calendar selectedDay, boolean scrollTo) {
        this.selectedDay = selectedDay;
        monthView.setSelectedDay(selectedDay, scrollTo);
        updateMonthAndYearTitle(selectedDay);
    }

    public void expand() {
        if (monthView != null && isCanCollapse) {
            monthView.setVisibility(VISIBLE);
            monthYearLabel.setCompoundDrawables(null, null, arrowUpDrawable, null);
            isCalendarCollapsed = false;
            if (listener != null)
                listener.onExpand();
        }
    }

    public void collapse() {
        if (monthView != null && isCanCollapse) {
            monthView.setVisibility(GONE);
            monthYearLabel.setCompoundDrawables(null, null, arrowDownDrawable, null);
            isCalendarCollapsed = true;
            if (listener != null)
                listener.onCollapse();
        }
    }

    public boolean isCollapsed() {
        return this.isCalendarCollapsed;
    }

    public void setIsCalendarCollapsed(boolean isCalendarCollapsed) {
        this.isCalendarCollapsed = isCalendarCollapsed;
    }

    public void setMonthCalendarExtendedListener(MonthCalendarExtendedListener listener) {
        this.listener = listener;
    }

    private class TextProcessor extends HandlerThread {
        private Handler handler;

        TextProcessor(String name) {
            super(name, Process.THREAD_PRIORITY_BACKGROUND);
        }

        void postTask(Runnable task) {
            handler.post(task);
        }

        void prepareHandler() {
            handler = new Handler(getLooper());
        }
    }

    private class TextTask implements Runnable {
        private Calendar day;

        public void setDay(Calendar day) {
            this.day = day;
        }

        @Override
        public void run() {
            String month = WordUtils.capitalize(DateUtils.dateToString(day.getTime(), MONTH_FORMAT));
            int monthLength = month.length();

            Spannable monthSpannableText = new SpannableString(month + " " + DateUtils.dateToString(day.getTime(), YEAR_FORMAT));
            int wholeLength = monthSpannableText.length();

            monthSpannableText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.month_title_color)), 0, monthLength, 0);

            monthSpannableText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.month_title_color)), monthLength, wholeLength, 0);

            final Spannable resultText = monthSpannableText;

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    monthYearLabel.setText(resultText);
                }
            });
        }
    }
}
