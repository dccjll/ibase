package com.dcc.ibase.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：录音工具
 */
object RecordAudioUtils {

    private const val TAG = "RecordAudioUtils"

    /**
     * 检测是否能录音
     */
    fun canRecordAudio(): Boolean {
        val minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val point = ShortArray(minBuffer)
        var readSize = 0
        var audioRecord: AudioRecord? = null
        try {
            audioRecord = AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    minBuffer * 100)
            // 开始录音
            audioRecord.startRecording()// 检测是否可以进入初始化状态
        } catch (e: Exception) {
            Log.e(TAG, "catch, 捕捉到异常, 无录音权限, e = " + e.message)
            if (audioRecord != null) {
                audioRecord.release()
                Log.i(TAG, "catch, 返回对象非空,释放资源")
            } else {
                Log.i(TAG, "catch, 返回对象非空")
            }
            return false
        }

        // 检测是否在录音中
        if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            // 6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            audioRecord.stop()
            audioRecord.release()
            Log.e(TAG, "无法启动录音, 无法录音")
            return false
        } else {// 正在录音
            readSize = audioRecord.read(point, 0, point.size)
            // 检测是否可以获取录音结果
            if (readSize <= 0) {
                audioRecord.stop()
                audioRecord.release()
                Log.e(TAG, "没有获取到录音数据，无录音权限")
                return false
            } else {
                audioRecord.stop()
                audioRecord.release()
                Log.i(TAG, "获取到录音数据, 有录音权限")
                return true
            }
        }
    }
}
