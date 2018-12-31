package com.jie.space.nettychat.config.rest;

import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by huangmingjie on 2017/10/17.
 */
@Configuration
public class RestTemplateConfig {

    @Autowired
    private RestProperties properties;

    @Bean(name = "sRestTemplate")
    public RestTemplate sRestTemplate() {
        return new RestTemplate(httpRequestFactory(properties.getSocketTimeout()));
    }

    @Bean(name = "lRestTemplate")
    public RestTemplate lRestTemplate() {
        return new RestTemplate(httpRequestFactory(properties.getSocketTimeout()));
    }

    private HttpComponentsClientHttpRequestFactory httpRequestFactory(int socketTimeout) {
        PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
        phccm.setMaxTotal(properties.getMaxTotle());
        phccm.setDefaultMaxPerRoute(properties.getMaxPerRoute());
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClientBuilder.create().setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setConnectionManager(phccm).build());
        factory.setConnectionRequestTimeout(properties.getConnectRequestTimeout());
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(socketTimeout);
        return factory;
    }

}
