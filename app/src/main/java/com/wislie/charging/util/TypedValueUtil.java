package com.wislie.charging.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-01 10:52
 * desc   : dp,px等相互转换
 * version: 1.0
 */
public class TypedValueUtil {

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return px的值
     */
    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context
     * @param sp
     * @return
     */
    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }
}
