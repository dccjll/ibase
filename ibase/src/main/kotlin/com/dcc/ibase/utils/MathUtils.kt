package com.dcc.ibase.utils

/**
 * 定禅天 净琉璃
 * 2018-11-21 21:23:42 Wednesday
 * 描述：数学工具
 */
object MathUtils {

    /**
     * 快速计算a的b次方然后与m取模
     */
    fun montgomery(a: Long, b: Long, m: Long): Long {
        var a = a
        var b = b
        var r: Long = 1
        a %= m
        while (b > 1) {
            if (b and 1 != 0L)
                r = r * a % m
            a = a * a % m
            b /= 2
        }
        return r * a % m
    }
}
