package io.github.memfis19.cadar.internal.ui.list.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.text.SimpleDateFormat;

import io.github.memfis19.cadar.R;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.internal.ui.events.TimeOutClickListener;
import io.github.memfis19.cadar.internal.ui.list.adapter.decorator.EventDecorator;
import io.github.memfis19.cadar.internal.ui.list.adapter.model.ListItemModel;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.internal.utils.ViewUtils;


/**
 * Created by memfis on 9/5/16.
 */
public class EventHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView dayLabel;
    private ImageView synchronizeButton;
    private TextView indicators;
    private TextView eventTime;

    private final String DAY_NUMBER_FORMAT = "dd";
    private final String WEEK_DAY_FORMAT = "EEE";
    private final String TIME_FORMAT = "HH:mm";

    private Handler backgroundHandler;
    private Handler uiHandler;

    private static Drawable serverSyncIcon;
    private static Drawable editIndicatorIcon;
    private static int syncEditIconsSizePx = 0;
    private static int smileSizePx = 0;
    private static String allDayLabelText;

    private Event event;
    private int position;

    public EventHolder(View itemView, Handler backgroundHandler, Handler uiHandler, final OnEventClickListener onEventClickListener) {
        super(itemView);
        if (syncEditIconsSizePx == 0)
            syncEditIconsSizePx = ViewUtils.convertDipToPixels(20, itemView.getContext());

        if (smileSizePx == 0)
            smileSizePx = ViewUtils.convertDipToPixels(24, itemView.getContext());

        if (serverSyncIcon == null) {
            serverSyncIcon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.item_sync_ico);
            double multiplier = (double) serverSyncIcon.getIntrinsicWidth() / serverSyncIcon.getIntrinsicHeight();
            serverSyncIcon.setBounds(0, 0, (int) (syncEditIconsSizePx * multiplier), syncEditIconsSizePx);
        }

        if (editIndicatorIcon == null) {
            editIndicatorIcon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.item_edit_ico);
            double multiplier = (double) editIndicatorIcon.getIntrinsicWidth() / editIndicatorIcon.getIntrinsicHeight();
            editIndicatorIcon.setBounds(0, 0, (int) (syncEditIconsSizePx * multiplier), syncEditIconsSizePx);
        }

        if (TextUtils.isEmpty(allDayLabelText))
            allDayLabelText = itemView.getContext().getResources().getString(R.string.default_all_day_label);

        indicators = (TextView) itemView.findViewById(R.id.indicators_owner_synchronize);
        title = (TextView) itemView.findViewById(R.id.event_title);
        synchronizeButton = (ImageView) itemView.findViewById(R.id.calendar_sync_icon);
        dayLabel = (TextView) itemView.findViewById(R.id.day_title);
        eventTime = (TextView) itemView.findViewById(R.id.event_time);

        this.backgroundHandler = backgroundHandler;
        this.uiHandler = uiHandler;

        itemView.setOnClickListener(new TimeOutClickListener() {
            @Override
            public void onViewClick(View view) {
                if (onEventClickListener != null)
                    onEventClickListener.onEventClick(event, position);
            }
        });

        if (synchronizeButton != null) {
            synchronizeButton.setOnClickListener(new TimeOutClickListener() {
                @Override
                public void onViewClick(View view) {
                    if (onEventClickListener != null) {
                        onEventClickListener.onSyncClick(event, position);
                    }
                }
            });
        }
    }

    public void bindView(final Event event, final ListItemModel previous, int position, @Nullable EventDecorator eventDecorator) {
        if (eventDecorator != null) {
            eventDecorator.onBindEventView(itemView, event, previous, position);
        } else {
            this.event = event;
            this.position = position;
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    final boolean displayDayIndicator;
                    if (previous != null) {
                        if (previous.getType() == ListItemModel.EVENT) {
                            Event before = (Event) previous.getValue();
                            displayDayIndicator = !DateUtils.isSameDay(event.getEventStartDate(), before.getEventStartDate());
                        } else {
                            displayDayIndicator = true;
                        }
                    } else {
                        displayDayIndicator = true;
                    }
                    final Spannable dayLabelText;
                    if (displayDayIndicator) {
                        String dayNumber = DateFormat.format(DAY_NUMBER_FORMAT, event.getEventStartDate()).toString();
                        int dayNumberLength = dayNumber.length();
                        dayLabelText = new SpannableString(dayNumber + "\n" + DateFormat.format(WEEK_DAY_FORMAT, event.getEventStartDate()).toString());
                        int wholeLength = dayLabelText.length();
                        dayLabelText.setSpan(new RelativeSizeSpan(0.5f), dayNumberLength, wholeLength, 0);

                    } else dayLabelText = new SpannableString("");

                    final Spannable eventTitle = new SpannableString(event.getEventTitle());

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (displayDayIndicator) {
                                EventHolder.this.dayLabel.setVisibility(View.VISIBLE);
                                EventHolder.this.dayLabel.setText(dayLabelText);
                            } else EventHolder.this.dayLabel.setVisibility(View.INVISIBLE);

                            title.setText(eventTitle);
                        }
                    });
                }
            });

            //Synchronize button
            updateSyncButton();

            if (!TextUtils.isEmpty(event.getEventIconUrl())) {
                Glide.with(title.getContext()).load(event.getEventIconUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(title.getContext().getResources(), resource);
                        double multiplier = (double) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                        drawable.setBounds(0, 0, (int) (smileSizePx * multiplier), smileSizePx);
                        title.setCompoundDrawables(drawable, null, null, null);
                    }
                });
            } else title.setCompoundDrawables(null, null, null, null);

            Drawable sync, edit;
            sync = null;

            if (event.isEditable()) edit = editIndicatorIcon;
            else edit = null;

            indicators.setCompoundDrawables(sync, null, edit, null);

            SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, DateUtils.getLocale());

            if (event.isAllDayEvent()) {
                eventTime.setText(allDayLabelText);
            } else if (event.getEventEndDate() != null && !event.getEventEndDate().equals(DateUtils.setTimeToEndOfTheDay(event.getEventEndDate()))) {
                eventTime.setText(timeFormat.format(event.getEventStartDate()) + " - " +
                        timeFormat.format(event.getEventEndDate()));
            } else eventTime.setText(timeFormat.format(event.getEventStartDate()));
        }
    }

    private void updateSyncButton() {
        if (event.getCalendarId() == null || event.getCalendarId() == 0)
            synchronizeButton.setAlpha(0.4f);
        else
            synchronizeButton.setAlpha(1f);
    }
}
