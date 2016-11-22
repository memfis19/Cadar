package io.github.memfis19.cadar.data.entity.property;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by memfis on 11/21/16.
 */

public final class EventProperties {

    public static final int NONE = 1;
    public static final int EVERY_WEEK = 2;
    public static final int EVERY_2_WEEK = 3;
    public static final int EVERY_3_WEEK = 4;
    public static final int EVERY_4_WEEK = 5;
    public static final int EVERY_MONTH = 6;
    public static final int EVERY_YEAR = 7;


    @IntDef({NONE, EVERY_WEEK, EVERY_2_WEEK, EVERY_3_WEEK,
            EVERY_4_WEEK, EVERY_MONTH, EVERY_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatPeriod {
    }
}
