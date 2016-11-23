package io.github.memfis19.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.ExtendedMonthCalendar;
import io.github.memfis19.sample.model.EventModel;

/**
 * Created by memfis on 11/23/16.
 */

public class ExtendedCalendarActivity extends AppCompatActivity {

    private ExtendedMonthCalendar extendedMonthCalendar;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_extended_month_calendar_activity);

        events.add(new EventModel());

        extendedMonthCalendar = (ExtendedMonthCalendar) findViewById(R.id.extended_calendar);

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder(this);
        extendedMonthCalendar.setup(builder.build(), Calendar.getInstance(), Calendar.MONDAY, events, getResources().getColor(R.color.material_light_background_color));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        extendedMonthCalendar.release();
    }
}
