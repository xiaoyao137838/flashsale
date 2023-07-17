package com.example.flashsaleproject.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.timeout}")
	private int timeout;
	
	@Value("${spring.redis.pool.max-active}")
	private int maxActive;
	
	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;
	
	@Value("${spring.redis.pool.min-idle}")
	private int minIdle;
	
	@Value("${spring.redis.pool.max-wait}")
	private long maxWaitMillis;
	
	@Bean
	public JedisPool redisPoolFactory() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxTotal(maxActive);
		jedisPoolConfig.setMinIdle(minIdle);
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, 
				port, timeout, null);
		
		logger.info("JedisPool is created successfully");
		logger.info("redis: " + host + ":" + port);
		return jedisPool;
		
	}

}
