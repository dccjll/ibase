package com.dcc.ibase.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：吐司工具
 */
object ToastUtils {
    private const val COLOR_DEFAULT = -0x1000001
    private val HANDLER = Handler(Looper.getMainLooper())

    private var sToast: Toast? = null
    private var sGravity = -1
    private var sXOffset = -1
    private var sYOffset = -1
    private var sBgColor = COLOR_DEFAULT
    private var sBgResource = -1
    private var sMsgColor = COLOR_DEFAULT

    /**
     * 设置吐司位置
     *
     * @param gravity 位置
     * @param xOffset x 偏移
     * @param yOffset y 偏移
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        sGravity = gravity
        sXOffset = xOffset
        sYOffset = yOffset
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景色
     */
    fun setBgColor(@ColorInt backgroundColor: Int) {
        sBgColor = backgroundColor
    }

    /**
     * 设置背景资源
     *
     * @param bgResource 背景资源
     */
    fun setBgResource(@DrawableRes bgResource: Int) {
        sBgResource = bgResource
    }

    /**
     * 设置消息颜色
     *
     * @param msgColor 颜色
     */
    fun setMsgColor(@ColorInt msgColor: Int) {
        sMsgColor = msgColor
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源 Id
     */
    fun showShort(@StringRes resId: Int) {
        show(resId, Toast.LENGTH_SHORT)
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源 Id
     * @param gravity 对齐方式
     */
    fun showShort(@StringRes resId: Int, gravity: Int) {
        setGravity(gravity, -1, -1)
        showShort(resId)
    }

    /**
     * 安全地显示短时吐司
     *
     * @param string 要显示的字符串
     */
    fun showShort(string: String?) {
        show(string, Toast.LENGTH_SHORT)
    }

    /**
     * 安全第显示短时吐司
     *
     * @param string 要显示的字符串
     * @param gravity 对齐方式
     */
    fun showShort(string: String?, gravity: Int) {
        setGravity(gravity, -1, -1)
        showShort(string)
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     */
    fun showLong(@StringRes resId: Int) {
        show(resId, Toast.LENGTH_LONG)
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     * @param gravity 对齐方式
     */
    fun showLong(@StringRes resId: Int, gravity: Int) {
        setGravity(gravity, -1, -1)
        show(resId, Toast.LENGTH_LONG)
    }

    /**
     * 安全地显示长时吐司
     *
     * @param string 要显示的字符串
     */
    fun showLong(string: String) {
        show(string, Toast.LENGTH_LONG)
    }

    /**
     * 安全地显示长时吐司
     *
     * @param string 要显示的字符串
     * @param gravity 对齐方式
     */
    fun showLong(string: String, gravity: Int) {
        setGravity(gravity, -1, -1)
        show(string, Toast.LENGTH_LONG)
    }

    /**
     * 安全地显示短时自定义吐司
     */
    fun showCustomShort(@LayoutRes layoutId: Int): View? {
        val view = getView(layoutId)
        show(view, Toast.LENGTH_SHORT)
        return view
    }

    /**
     * 安全地显示长时自定义吐司
     */
    fun showCustomLong(@LayoutRes layoutId: Int): View? {
        val view = getView(layoutId)
        show(view, Toast.LENGTH_LONG)
        return view
    }

    /**
     * 取消吐司显示
     */
    fun cancel() {
        if (sToast != null) {
            sToast!!.cancel()
            sToast = null
        }
    }

    private fun show(@StringRes resId: Int, duration: Int) {
        show(AppUtils.app.resources.getText(resId).toString(), duration)
    }

    private fun show(text: CharSequence?, duration: Int) {
        HANDLER.post {
            cancel()
            sToast = Toast.makeText(AppUtils.app, text, duration)
            val tvMessage = sToast!!.view.findViewById<TextView>(android.R.id.message)
            val msgColor = tvMessage.currentTextColor
            //it solve the font of toast
            TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance)
            if (sMsgColor != COLOR_DEFAULT) {
                tvMessage.setTextColor(sMsgColor)
            } else {
                tvMessage.setTextColor(msgColor)
            }
            if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                sToast!!.setGravity(sGravity, sXOffset, sYOffset)
            }
            setBg(tvMessage)
            sToast!!.show()
        }
    }

    private fun show(view: View?, duration: Int) {
        HANDLER.post {
            cancel()
            sToast = Toast(AppUtils.app)
            sToast!!.view = view
            sToast!!.duration = duration
            if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                sToast!!.setGravity(sGravity, sXOffset, sYOffset)
            }
            setBg()
            sToast!!.show()
        }
    }

    private fun setBg() {
        val toastView = sToast!!.view
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource)
        } else if (sBgColor != COLOR_DEFAULT) {
            val background = toastView.background
            if (background != null) {
                background.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else {
                ViewCompat.setBackground(toastView, ColorDrawable(sBgColor))
            }
        }
    }

    private fun setBg(tvMsg: TextView) {
        val toastView = sToast!!.view
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource)
            tvMsg.setBackgroundColor(Color.TRANSPARENT)
        } else if (sBgColor != COLOR_DEFAULT) {
            val tvBg = toastView.background
            val msgBg = tvMsg.background
            if (tvBg != null && msgBg != null) {
                tvBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
                tvMsg.setBackgroundColor(Color.TRANSPARENT)
            } else if (tvBg != null) {
                tvBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else if (msgBg != null) {
                msgBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else {
                toastView.setBackgroundColor(sBgColor)
            }
        }
    }

    private fun getView(@LayoutRes layoutId: Int): View? {
        val inflate = AppUtils.app.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflate.inflate(layoutId, null)
    }
}
