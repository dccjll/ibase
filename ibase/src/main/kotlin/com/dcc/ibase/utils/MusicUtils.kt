package com.dcc.ibase.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

/**
 * 定禅天 净琉璃
 * 2018-11-26 08:50:54 Monday
 * 描述：音乐工具
 */
object MusicUtils {
    private val `object` = Any()
    private var mMediaPlayer: MediaPlayer? = null

    /**
     * 播放音乐
     */
    fun playMusic(context: Context, path: String) {
        synchronized(`object`) {
            try {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer()
                }
                mMediaPlayer!!.stop()
                mMediaPlayer!!.reset()
                mMediaPlayer!!.setDataSource(context, Uri.parse(path))
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 停止播放
     */
    fun stopMusic() {
        synchronized(`object`) {
            if (mMediaPlayer != null) {
                mMediaPlayer!!.stop()
                mMediaPlayer!!.reset()
            }
        }
    }
}
