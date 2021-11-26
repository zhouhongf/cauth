package com.myworld.cauth.config;

import feign.Client;
import feign.Feign;
import feign.Logger;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignConfig {

    /**
     * Logger.Level 的具体级别如下：
     * NONE：不记录任何信息
     * BASIC：仅记录请求方法、URL以及响应状态码和执行时间
     * HEADERS：除了记录 BASIC级别的信息外，还会记录请求和响应的头信息
     * FULL：记录所有请求与响应的明细，包括头信息、请求体、元数据
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    private okhttp3.OkHttpClient okHttpClient;

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool httpClientConnectionPool(
        FeignHttpClientProperties httpClientProperties,
        OkHttpClientConnectionPoolFactory connectionPoolFactory) {
        Integer maxTotalConnections = httpClientProperties.getMaxConnections();
        Long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
    }

    @Bean
    public okhttp3.OkHttpClient client(OkHttpClientFactory httpClientFactory,
                                       ConnectionPool connectionPool,
                                       FeignHttpClientProperties httpClientProperties) {
        Boolean followRedirects = httpClientProperties.isFollowRedirects();
        Integer connectTimeout = httpClientProperties.getConnectionTimeout();
        Boolean disableSslValidation = httpClientProperties.isDisableSslValidation();
        this.okHttpClient = httpClientFactory.createBuilder(disableSslValidation)
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .followRedirects(followRedirects).connectionPool(connectionPool)
            .addInterceptor(feignOkHttpInterceptor())
            .build();
        return this.okHttpClient;
    }

    @PreDestroy
    public void destroy() {
        if (this.okHttpClient != null) {
            this.okHttpClient.dispatcher().executorService().shutdown();
            this.okHttpClient.connectionPool().evictAll();
        }
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client feignClient(okhttp3.OkHttpClient client) {
        return new feign.okhttp.OkHttpClient(client);
    }

    @Bean
    public Interceptor feignOkHttpInterceptor() {
        return new FeignOkHttpInterceptor();
    }

}
