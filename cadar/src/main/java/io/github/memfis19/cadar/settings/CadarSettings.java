package io.github.memfis19.cadar.settings;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/**
 * Created by memfis on 11/21/16.
 */

public final class CadarSettings {
    public static final int STATE_NOT_READY = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_PREPARE = 2;


    @IntDef({Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
            Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DayOfWeeks {
    }

    @IntDef({STATE_NOT_READY, STATE_PREPARE, STATE_READY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CalendarState {
    }
}
