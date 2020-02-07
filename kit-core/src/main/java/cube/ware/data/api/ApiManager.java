package cube.ware.data.api;

import android.support.annotation.NonNull;
import android.util.Log;
import com.common.utils.utils.log.LogUtil;
import cube.ware.core.CubeCore;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * api网络请求管理器
 *
 * @author LiuFeng
 * @data 2020/2/3 18:23
 */
public class ApiManager {
    private static final String TAG = "ApiManager";

    private static ApiManager mInstance = new ApiManager();

    private static final int READ_TIME_OUT    = 15;      // 读取超时时间为15秒
    private static final int CONNECT_TIME_OUT = 15;      // 连接超时时间为15秒

    private Retrofit     mRetrofit;         // Retrofit实例
    private OkHttpClient mOkHttpClient;     // OkHttpClient实例
    private ApiService   mApiService;       // api接口

    /**
     * 单例
     */
    public static ApiManager getInstance() {
        return mInstance;
    }

    /**
     * 构造方法
     */
    private ApiManager() {
        this.initOkHttp();
        this.initRetrofit();
        this.initApiService();
    }

    /**
     * 初始化OkHttp
     */
    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 打印请求log日志
        if (CubeCore.getInstance().isDebug()) {
            builder.addInterceptor(getLogInterceptor());
        }
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS); // 设置连接超时
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);   // 设置读取超时
        builder.writeTimeout(READ_TIME_OUT, TimeUnit.SECONDS);  // 设置写入超时
        builder.retryOnConnectionFailure(true); // 设置重连
        this.setSSL(builder);
        this.mOkHttpClient = builder.build();
    }

    /**
     * 日志拦截
     *
     * @return
     */
    private Interceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.i("OkHttp:", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        // base地址
        builder.baseUrl(CubeCore.getInstance().getUserCenterUrl());
        builder.client(this.mOkHttpClient);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        this.mRetrofit = builder.build();
    }

    /**
     * 初始化Api
     */
    private void initApiService() {
        this.mApiService = mRetrofit.create(ApiService.class);
    }

    /**
     * 获取ApiService
     */
    ApiService getApiService() {
        return this.mApiService;
    }

    /**
     * 设置忽略ssl证书验证
     *
     * @param builder
     */
    private void setSSL(OkHttpClient.Builder builder) {
        try {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { xtm }, new SecureRandom());

            HostnameVerifier doNotVerify = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            builder.sslSocketFactory(sslContext.getSocketFactory(), xtm);
            builder.hostnameVerifier(doNotVerify);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LogUtil.e(TAG, e);
        }
    }
}
