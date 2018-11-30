package com.dcc.ibase.view.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.dcc.ibase.utils.AppUtils;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * 定禅天 净琉璃
 * 2018-11-22 13:08:39 Thursday
 * 描述：Glide默认缓存
 */
public class GlideCache implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        // 设置磁盘缓存目录（和创建的缓存目录相同）
        String cacheDirectory = AppUtils.Companion.getApp().getCacheDir().getAbsolutePath() + "/GlideCache/";
        // 设置缓存的大小为100M
        int cacheSize = 100*1000*1000;
        builder.setDiskCache(new DiskLruCacheFactory(cacheDirectory, cacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

}
