package com.common.utils.utils.glide;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

/**
 * SSLSocket配置
 *
 * @author LiuFeng
 * @data 2019/4/18 10:21
 */
public class SslSocketClient {
    /**
     * 获取这个SSLSocketFactory
     *
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { getTrustManager() }, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取TrustManager
     *
     * @return
     */
    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
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
    }

    /**
     * 获取HostnameVerifier
     *
     * @return
     */
    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    /**
     * 获取忽略ssl的OkHttp
     */
    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS); // 设置连接超时
        builder.readTimeout(15, TimeUnit.SECONDS);   // 设置读取超时
        builder.writeTimeout(15, TimeUnit.SECONDS);  // 设置写入超时
        builder.retryOnConnectionFailure(true); // 设置重连
        builder.sslSocketFactory(getSSLSocketFactory(), getTrustManager());
        builder.hostnameVerifier(getHostnameVerifier());
        return builder.build();
    }
}
