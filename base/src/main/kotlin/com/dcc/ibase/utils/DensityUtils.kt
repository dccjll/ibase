package com.dcc.ibase.utils

import android.content.Context
import android.util.TypedValue

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：密度工具
 */
object DensityUtils {
    /**
     * dp转px
     */
    fun dp2px(context: Context?, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context?.resources?.displayMetrics).toInt()
    }


    /**
     * sp转px
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.resources.displayMetrics).toInt()
    }
}
