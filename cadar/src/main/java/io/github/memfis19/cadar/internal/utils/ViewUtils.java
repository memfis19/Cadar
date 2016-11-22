package io.github.memfis19.cadar.internal.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

/**
 * Created by memfis on 4/1/15.
 */
public final class ViewUtils {

    public static int convertDipToPixels(int dip, Context context) {
        Resources resources = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
        return (int) px;
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getResources().getDrawable(drawableId, context.getTheme());
        } else {
            return context.getResources().getDrawable(drawableId);
        }
    }

}
