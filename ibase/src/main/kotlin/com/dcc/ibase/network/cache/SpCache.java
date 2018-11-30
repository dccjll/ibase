package com.dcc.ibase.network.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.dcc.ibase.log.LogManager;
import com.dcc.ibase.network.VHttpStatics;
import com.dcc.ibase.utils.Base64Utils;
import com.dcc.ibase.utils.ByteUtils;

/**
 * @Description: SharedPreferences存储，支持对象加密存储
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:12
 */
public class SpCache implements ICache {
    private SharedPreferences sp;

    public SpCache(Context context) {
        this(context, VHttpStatics.CACHE_SP_NAME);
    }

    public SpCache(Context context, String fileName) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSp() {
        return sp;
    }

    @Override
    public void put(String key, Object ser) {
        try {
            LogManager.Companion.i(key + " put: " + ser);
            if (ser == null) {
                sp.edit().remove(key).apply();
            } else {
                byte[] bytes = ByteUtils.INSTANCE.parseObjectToBytes(ser);
                bytes = Base64Utils.INSTANCE.encode(bytes);
                put(key, ByteUtils.INSTANCE.parseBytesToHexString(bytes, true, " "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        try {
            String hex = get(key, null);
            if (hex == null) return null;
            byte[] bytes = ByteUtils.INSTANCE.parseHexStringToBytes(hex);
            bytes = Base64Utils.INSTANCE.decode(bytes);
            Object obj = ByteUtils.INSTANCE.parseBytesToObject(bytes);
            LogManager.Companion.i(key + " get: " + obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        return sp.contains(key);
    }

    @Override
    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    @Override
    public void clear() {
        sp.edit().clear().apply();
    }

    public void put(String key, String value) {
        if (value == null) {
            sp.edit().remove(key).apply();
        } else {
            sp.edit().putString(key, value).apply();
        }
    }

    public void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public void put(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }
}
