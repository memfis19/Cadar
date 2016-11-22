package io.github.memfis19.cadar.internal.ui.list.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Calendar;

import io.github.memfis19.cadar.R;

/**
 * Created by memfis on 9/5/16.
 */
public class ListItemHolder extends RecyclerView.ViewHolder {

    private final String DATE_FORMAT = "MMMM yyyy";

    private TextView monthTitle;
    private ImageView monthBackground;

    private Handler backgroundHandler;
    private Handler uiHandler;

    private View itemView;

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            monthBackground.scrollBy(dx, (-1) * (dy / 10));
        }
    };

    public RecyclerView.OnScrollListener getScrollListener() {
        return scrollListener;
    }

    public ListItemHolder(View itemView, Handler backgroundHandler, Handler uiHandler) {
        super(itemView);

        this.itemView = itemView;
        this.backgroundHandler = backgroundHandler;
        this.uiHandler = uiHandler;

        monthTitle = (TextView) itemView.findViewById(R.id.month_label);
        monthBackground = (ImageView) itemView.findViewById(R.id.month_background);
    }

    public void bindView(final Calendar month) {
        monthBackground.setImageDrawable(null);
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                final Spannable date = new SpannableString(DateFormat.format(DATE_FORMAT, month.getTime()).toString());

                final int backgroundId = getBackgroundId(month.get(Calendar.MONTH));
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        monthTitle.setText(date);
                        Glide.with(monthTitle.getContext().getApplicationContext()).load(backgroundId).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Drawable background = new BitmapDrawable(monthBackground.getContext().getResources(), resource);
                                monthBackground.setImageDrawable(background);
                                monthBackground.setScrollX(0);
                                monthBackground.setScrollY(0);
                            }
                        });
                    }
                });
            }
        });
    }

    private int getBackgroundId(int month) {
        int backgroundId = R.drawable.bkg_12_december;

        if (month == Calendar.JANUARY) {
            backgroundId = R.drawable.bkg_01_january;
        } else if (month == Calendar.FEBRUARY) {
            backgroundId = R.drawable.bkg_02_february;
        } else if (month == Calendar.MARCH) {
            backgroundId = R.drawable.bkg_03_march;
        } else if (month == Calendar.APRIL) {
            backgroundId = R.drawable.bkg_04_april;
        } else if (month == Calendar.MAY) {
            backgroundId = R.drawable.bkg_05_may;
        } else if (month == Calendar.JUNE) {
            backgroundId = R.drawable.bkg_06_june;
        } else if (month == Calendar.JULY) {
            backgroundId = R.drawable.bkg_07_july;
        } else if (month == Calendar.AUGUST) {
            backgroundId = R.drawable.bkg_08_august;
        } else if (month == Calendar.SEPTEMBER) {
            backgroundId = R.drawable.bkg_09_september;
        } else if (month == Calendar.OCTOBER) {
            backgroundId = R.drawable.bkg_10_october;
        } else if (month == Calendar.NOVEMBER) {
            backgroundId = R.drawable.bkg_11_november;
        }

        return backgroundId;
    }
}
