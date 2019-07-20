package com.tistory.lky1001.androidonvif.lib;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

class ServiceBuilder {

    private static final int CONNECTION_TIMEOUT_IN_SEC = 15;
    private static final int READ_TIMEOUT_IN_SEC = 15;
    private static final int WRITE_TIMEOUT_IN_SEC = 15;

    // No need to instantiate this class.
    private ServiceBuilder() {}

    public static <T> T createService(Class<T> serviceClass, String baseUrl, boolean userBadSslSocketFactory) {
        return createService(serviceClass, baseUrl, userBadSslSocketFactory,
                CONNECTION_TIMEOUT_IN_SEC, READ_TIMEOUT_IN_SEC, WRITE_TIMEOUT_IN_SEC);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl, boolean userBadSslSocketFactory,
                                      int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec) {
        OkHttpClient okHttpClient = getClient(userBadSslSocketFactory,
                connectTimeoutInSec, readTimeoutInSec, writeTimeoutInSec);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        retrofitBuilder
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
        retrofitBuilder.addConverterFactory(SimpleXmlConverterFactory.create());

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient getClient(boolean userBadSslSocketFactory,
                                          int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(connectTimeoutInSec, TimeUnit.SECONDS);
        builder.readTimeout(readTimeoutInSec, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeoutInSec, TimeUnit.SECONDS);

        if (userBadSslSocketFactory) {
            builder.sslSocketFactory(createBadSslSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        return builder.build();
    }

    private static SSLSocketFactory createBadSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            context.init(null, new TrustManager[]{permissive}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
