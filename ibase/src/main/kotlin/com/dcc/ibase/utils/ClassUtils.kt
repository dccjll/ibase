package com.dcc.ibase.utils

import java.lang.reflect.InvocationTargetException

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：类转换工具
 */
object ClassUtils {

    /**
     * 将父类所有的属性COPY到子类中<br>
     * 类定义中child一定要extends father<br>
     * 而且child和father一定为严格javabean写法，属性为deleteDate，方法为getDeleteDate
     */
    fun fatherToChild(father: Any, child: Any) {
        if (child.javaClass.superclass != father.javaClass) {
            System.err.println("child not father's subclass")
        }
        val fatherClass = father.javaClass
        val ff = fatherClass.declaredFields
        for (f in ff) {
            val type = f.type
            try {
                val m = fatherClass.getMethod("get" + upperHeadChar(f.name))//方法getDeleteDate
                f.isAccessible = true
                val obj = m.invoke(father)//取出属性值
                f.set(child, obj)
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 首字母大写，in:deleteDate，out:DeleteDate
     */
    private fun upperHeadChar(`in`: String): String {
        val head = `in`.substring(0, 1)
        return head.toUpperCase() + `in`.substring(1, `in`.length)
    }
}
