package com.example.flashsaleproject.redis;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.models.Product;
import com.google.gson.Gson;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private JedisPool jedisPool;
	
	private Jedis jedisClient;
	
	Gson gson = new Gson();

	public RedisService(JedisPool jedisPool) {
		super();
		this.jedisPool = jedisPool;
	}
	
	@PostConstruct
	public void initalize() {
		jedisClient = jedisPool.getResource();
	}
	
	@PreDestroy
	public void close() {
		jedisClient.close();
	}

	public void addFlashSaleInfo(long flashSaleId, FlashSale flashSale) {
		String flashSaleKey = "java-flash:flashsale:" + flashSaleId;
		String value = gson.toJson(flashSale);
		jedisClient.set(flashSaleKey, value);
		
	}
	
	public void addProductInfo(long productId, Product product) {
		String productKey = "java-flash:product:" + productId;
		String value = gson.toJson(product);
		jedisClient.set(productKey, value);
		
	}
	
	public void addFlashSaleQuantity(long flashSaleId, int totalQuantity) {
		String flashSaleCountKey = "java-flash:count:" + flashSaleId;
		jedisClient.set(flashSaleCountKey, String.valueOf(totalQuantity));
	}
	
	public String getFlashSaleQuantity(long flashSaleId) {
		String flashSaleCountKey = "java-flash:count:" + flashSaleId;
		return jedisClient.get(flashSaleCountKey);
	}
	
	public void addOversellQuantity(long flashSaleId, int totalQuantity) {
		String flashSaleCountKey = "java-flash:oversell:" + flashSaleId;
		jedisClient.set(flashSaleCountKey, String.valueOf(totalQuantity));
	}
	
	public String getOversellQuantity(long flashSaleId) {
		String flashSaleCountKey = "java-flash:oversell:" + flashSaleId;
		return jedisClient.get(flashSaleCountKey);
	}
	
	public void addLimitMember(long flashSaleId, long userId) {
		String flashSaleGroupKey = "java-flash:group:" + flashSaleId;
		jedisClient.sadd(flashSaleGroupKey, String.valueOf(userId));
		
	}
	
	public void removeLimitMember(long flashSaleId, long userId) {
		String flashSaleGroupKey = "java-flash:group:" + flashSaleId;
		jedisClient.srem(flashSaleGroupKey, String.valueOf(userId));
	}

	public boolean isInLimitMember(long flashSaleId, long userId) {
		String flashSaleGroupKey = "java-flash:group:" + flashSaleId;
		if (jedisClient.sismember(flashSaleGroupKey, String.valueOf(userId))) {
			return true;
		}
		return false;
	}


	public boolean lockStock(long flashSaleId) {
		try {
		
			String flashSaleKey = "java-flash:count:" + flashSaleId;
			String script = 
					"""
						if redis.call('exists', KEYS[1]) == 1 then
								local stock = tonumber(redis.call('get', KEYS[1]))
								if stock <= 0 then
										return -1
								end;
								redis.call('decr', KEYS[1]);
								return stock - 1;
						end;
						return -1;
					""";
			long stock = (Long) jedisClient.eval(script, Collections.singletonList
			(flashSaleKey), Collections.emptyList());
			
			if (stock < 0) {
				logger.warn("Stock is not enough");
				return false;
			} 
			
			logger.info("Purchase successfully");
			return true;
		} catch(Throwable throwable) {
			logger.warn("Stock is not locked  " + throwable.toString());
			return false;
		}
	}
	
	public void invertStock(String flashSaleId) {
		String flashSaleKey = "java-flash:count:" + flashSaleId;
		jedisClient.incr(flashSaleKey);
	}

	public String getFlashSaleInfo(String flashSaleKey) {
		
		return jedisClient.get(flashSaleKey);
	}

	public String getProductInfo(String productKey) {
		
		return jedisClient.get(productKey);
	}

}
