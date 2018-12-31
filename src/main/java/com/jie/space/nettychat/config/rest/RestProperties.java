package com.jie.space.nettychat.config.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by huangmingjie on 2017/10/17.
 */
@Component
@Data
@ConfigurationProperties(prefix = "http.pool")
public class RestProperties {

	private int maxTotle;
	private int maxPerRoute;
	private int socketTimeout;
	private int connectTimeout;
	private int connectRequestTimeout;

}
