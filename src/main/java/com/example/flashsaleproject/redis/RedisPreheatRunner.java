package com.example.flashsaleproject.redis;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.models.FlashSale;

@Component
public class RedisPreheatRunner implements ApplicationRunner {
	
	RedisService redisService;
	
	FlashSaleRepository flashSaleRepository;

	public RedisPreheatRunner(RedisService redisService, FlashSaleRepository flashSaleRepository) {
		super();
		this.redisService = redisService;
		this.flashSaleRepository = flashSaleRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<FlashSale> flashSales = flashSaleRepository.findAll();
		for (FlashSale flashSale : flashSales) {
			long flashSaleId = flashSale.getId();
			int availableQuantity = flashSale.getAvailableQuantity();
			redisService.addFlashSaleQuantity(flashSaleId, availableQuantity);
		}
		
	}
	
}
