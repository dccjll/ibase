package com.dcc.ibase.network.request;

import com.dcc.ibase.log.LogManager;
import com.dcc.ibase.network.VHttp;
import com.dcc.ibase.network.callback.UCallback;
import com.dcc.ibase.network.VHttpGlobalConfig;
import com.dcc.ibase.network.VHttpStatics;
import com.dcc.ibase.network.core.ApiCookie;
import com.dcc.ibase.network.interceptor.HeadersInterceptor;
import com.dcc.ibase.network.interceptor.UploadProgressInterceptor;
import com.dcc.ibase.network.mode.ApiHost;
import com.dcc.ibase.network.mode.HttpHeaders;
import com.dcc.ibase.utils.SSLUtils;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: 请求基类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:05
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected VHttpGlobalConfig sVHttpGlobalConfig;//全局配置
    protected Retrofit retrofit;//Retrofit对象
    protected List<Interceptor> interceptors = new ArrayList<>();//局部请求的拦截器
    protected List<Interceptor> networkInterceptors = new ArrayList<>();//局部请求的网络拦截器
    protected HttpHeaders headers = new HttpHeaders();//请求头
    protected String baseUrl;//基础域名
    protected Object tag;//请求标签
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存
    protected UCallback uploadCallback;//上传进度回调

    /**
     * 设置基础域名，当前请求会替换全局域名
     *
     * @param baseUrl
     * @return
     */
    public R baseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl;
        }
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headerKey
     * @param headerValue
     * @return
     */
    public R addHeader(String headerKey, String headerValue) {
        this.headers.put(headerKey, headerValue);
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headers
     * @return
     */
    public R addHeaders(Map<String, String> headers) {
        this.headers.put(headers);
        return (R) this;
    }

    /**
     * 移除请求头
     *
     * @param headerKey
     * @return
     */
    public R removeHeader(String headerKey) {
        this.headers.remove(headerKey);
        return (R) this;
    }

    /**
     * 设置请求头
     *
     * @param headers
     * @return
     */
    public R headers(HttpHeaders headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return (R) this;
    }

    /**
     * 设置请求标签
     *
     * @param tag
     * @return
     */
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param connectTimeOut
     * @return
     */
    public R connectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return (R) this;
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param readTimeOut
     * @return
     */
    public R readTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param writeTimeOut
     * @return
     */
    public R writeTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    /**
     * 设置是否进行HTTP缓存
     *
     * @param isHttpCache
     * @return
     */
    public R setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return (R) this;
    }

    /**
     * 局部设置拦截器
     *
     * @param interceptor
     * @return
     */
    public R interceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
        return (R) this;
    }

    /**
     * 局部设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public R networkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            networkInterceptors.add(interceptor);
        }
        return (R) this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public long getWriteTimeOut() {
        return writeTimeOut;
    }

    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    public boolean isHttpCache() {
        return isHttpCache;
    }

    /**
     * 生成局部配置
     */
    protected void generateLocalConfig() {
        OkHttpClient.Builder newBuilder = VHttp.getOkHttpClient().newBuilder();

        if (sVHttpGlobalConfig.getGlobalHeaders() != null) {
            headers.put(sVHttpGlobalConfig.getGlobalHeaders());
        }

        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                newBuilder.addInterceptor(interceptor);
            }
        }

        if (!networkInterceptors.isEmpty()) {
            for (Interceptor interceptor : networkInterceptors) {
                newBuilder.addNetworkInterceptor(interceptor);
            }
        }

        if (headers.headersMap.size() > 0) {
            newBuilder.addInterceptor(new HeadersInterceptor(headers.headersMap));
        }

        if (uploadCallback != null) {
            newBuilder.addNetworkInterceptor(new UploadProgressInterceptor(uploadCallback));
        }

        if (readTimeOut > 0) {
            newBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        }

        if (writeTimeOut > 0) {
            newBuilder.readTimeout(writeTimeOut, TimeUnit.SECONDS);
        }

        if (connectTimeOut > 0) {
            newBuilder.readTimeout(connectTimeOut, TimeUnit.SECONDS);
        }

        if (isHttpCache) {
            try {
                if (sVHttpGlobalConfig.getHttpCache() == null) {
                    sVHttpGlobalConfig.httpCache(new Cache(sVHttpGlobalConfig.getHttpCacheDirectory(), VHttpStatics.CACHE_MAX_SIZE));
                }
                sVHttpGlobalConfig.cacheOnline(sVHttpGlobalConfig.getHttpCache());
                sVHttpGlobalConfig.cacheOffline(sVHttpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                LogManager.Companion.e("Could not create http cache" + e);
            }
            newBuilder.cache(sVHttpGlobalConfig.getHttpCache());
        }

        if (baseUrl != null) {
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            if (sVHttpGlobalConfig.getConverterFactory() != null) {
                newRetrofitBuilder.addConverterFactory(sVHttpGlobalConfig.getConverterFactory());
            }
            if (sVHttpGlobalConfig.getCallAdapterFactory() != null) {
                newRetrofitBuilder.addCallAdapterFactory(sVHttpGlobalConfig.getCallAdapterFactory());
            }
            if (sVHttpGlobalConfig.getCallFactory() != null) {
                newRetrofitBuilder.callFactory(sVHttpGlobalConfig.getCallFactory());
            }
            newBuilder.hostnameVerifier(new SSLUtils.UnSafeHostnameVerifier(baseUrl));
            newRetrofitBuilder.client(newBuilder.build());
            retrofit = newRetrofitBuilder.build();
        } else {
            VHttp.getRetrofitBuilder().client(newBuilder.build());
            retrofit = VHttp.getRetrofitBuilder().build();
        }
    }

    /**
     * 生成全局配置
     */
    protected void generateGlobalConfig() {
        sVHttpGlobalConfig = VHttp.CONFIG();

        if (sVHttpGlobalConfig.getBaseUrl() == null) {
            sVHttpGlobalConfig.baseUrl(ApiHost.getHost());
        }
        VHttp.getRetrofitBuilder().baseUrl(sVHttpGlobalConfig.getBaseUrl());

        if (sVHttpGlobalConfig.getConverterFactory() == null) {
            sVHttpGlobalConfig.converterFactory(GsonConverterFactory.create());
        }
        VHttp.getRetrofitBuilder().addConverterFactory(sVHttpGlobalConfig.getConverterFactory());

        if (sVHttpGlobalConfig.getCallAdapterFactory() == null) {
            sVHttpGlobalConfig.callAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        VHttp.getRetrofitBuilder().addCallAdapterFactory(sVHttpGlobalConfig.getCallAdapterFactory());

        if (sVHttpGlobalConfig.getCallFactory() != null) {
            VHttp.getRetrofitBuilder().callFactory(sVHttpGlobalConfig.getCallFactory());
        }

        if (sVHttpGlobalConfig.getHostnameVerifier() == null) {
            sVHttpGlobalConfig.hostnameVerifier(new SSLUtils.UnSafeHostnameVerifier(sVHttpGlobalConfig.getBaseUrl()));
        }
        VHttp.getOkHttpBuilder().hostnameVerifier(sVHttpGlobalConfig.getHostnameVerifier());

        if (sVHttpGlobalConfig.getSslSocketFactory() == null) {
            sVHttpGlobalConfig.SSLSocketFactory(SSLUtils.INSTANCE.getSslSocketFactory(null, null, null));
        }
        VHttp.getOkHttpBuilder().sslSocketFactory(sVHttpGlobalConfig.getSslSocketFactory());

        if (sVHttpGlobalConfig.getConnectionPool() == null) {
            sVHttpGlobalConfig.connectionPool(new ConnectionPool(VHttpStatics.DEFAULT_MAX_IDLE_CONNECTIONS,
                    VHttpStatics.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        VHttp.getOkHttpBuilder().connectionPool(sVHttpGlobalConfig.getConnectionPool());

        if (sVHttpGlobalConfig.isCookie() && sVHttpGlobalConfig.getApiCookie() == null) {
            sVHttpGlobalConfig.apiCookie(new ApiCookie(VHttp.getContext()));
        }
        if (sVHttpGlobalConfig.isCookie()) {
            VHttp.getOkHttpBuilder().cookieJar(sVHttpGlobalConfig.getApiCookie());
        }

        if (sVHttpGlobalConfig.getHttpCacheDirectory() == null) {
            sVHttpGlobalConfig.setHttpCacheDirectory(new File(VHttp.getContext().getCacheDir(), VHttpStatics.CACHE_HTTP_DIR));
        }
        if (sVHttpGlobalConfig.isHttpCache()) {
            try {
                if (sVHttpGlobalConfig.getHttpCache() == null) {
                    sVHttpGlobalConfig.httpCache(new Cache(sVHttpGlobalConfig.getHttpCacheDirectory(), VHttpStatics.CACHE_MAX_SIZE));
                }
                sVHttpGlobalConfig.cacheOnline(sVHttpGlobalConfig.getHttpCache());
                sVHttpGlobalConfig.cacheOffline(sVHttpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                LogManager.Companion.e("Could not create http cache" + e);
            }
        }
        if (sVHttpGlobalConfig.getHttpCache() != null) {
            VHttp.getOkHttpBuilder().cache(sVHttpGlobalConfig.getHttpCache());
        }
        VHttp.getOkHttpBuilder().connectTimeout(VHttpStatics.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        VHttp.getOkHttpBuilder().writeTimeout(VHttpStatics.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        VHttp.getOkHttpBuilder().readTimeout(VHttpStatics.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获取第一级type
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 获取次一级type(如果有)
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }
}
