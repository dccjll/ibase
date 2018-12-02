package com.dcc.ibase.utils

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal

/**
 * 定禅天 净琉璃
 * 2018-11-21 21:23:42 Wednesday
 * 描述：指纹识别工具
 */
object FingerUtils {

    private val fingerprintManagerCompat = FingerprintManagerCompat.from(AppUtils.app)
    private var cancellationSignal: CancellationSignal? = null

    /**
     * 设备是否支持指纹识别
     */
    val isHardwareDetected: Boolean
        get() = fingerprintManagerCompat.isHardwareDetected

    /**
     * 手机中是否存在指纹
     */
    fun hasEnrolledFingerprints(): Boolean {
        return fingerprintManagerCompat.hasEnrolledFingerprints()
    }

    /**
     * 请求验证指纹
     */
    fun authenticate(authenticationCallback: FingerprintManagerCompat.AuthenticationCallback) {
        cancellationSignal = CancellationSignal()
        fingerprintManagerCompat.authenticate(null, 0, cancellationSignal, authenticationCallback, null)
    }

    /**
     * 取消验证
     */
    fun cancelAuthenticate() {
        cancellationSignal!!.cancel()
        cancellationSignal = null
    }
}
