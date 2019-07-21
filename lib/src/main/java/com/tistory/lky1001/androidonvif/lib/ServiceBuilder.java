package com.tistory.lky1001.androidonvif.lib;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private static Credentials credentials;

    // No need to instantiate this class.
    private ServiceBuilder() {}

    public static void setCredentials(Credentials credentials) {
        ServiceBuilder.credentials = credentials;
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl, boolean userBadSslSocketFactory) {
        return createService(serviceClass, baseUrl, userBadSslSocketFactory,
                CONNECTION_TIMEOUT_IN_SEC, READ_TIMEOUT_IN_SEC, WRITE_TIMEOUT_IN_SEC);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl, boolean userBadSslSocketFactory,
                                      int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec) {
        OkHttpClient okHttpClient = getClient(userBadSslSocketFactory,
                connectTimeoutInSec, readTimeoutInSec, writeTimeoutInSec);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);

        retrofitBuilder
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient getClient(boolean userBadSslSocketFactory,
                                          int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(connectTimeoutInSec, TimeUnit.SECONDS)
                .readTimeout(readTimeoutInSec, TimeUnit.SECONDS)
                .writeTimeout(writeTimeoutInSec, TimeUnit.SECONDS);

        if (userBadSslSocketFactory) {
            builder.sslSocketFactory(createBadSslSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
        }

        if (credentials != null) {
            DigestAuthenticator authenticator = new DigestAuthenticator(credentials);
            Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

            builder.authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                    .addInterceptor(new AuthenticationCacheInterceptor(authCache));
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
