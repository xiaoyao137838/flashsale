package com.example.flashsaleproject.service;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.redis.RedisService;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class FlashSaleService {
	
	private RedisService redisService;
	
	private FlashSaleRepository flashSaleRepository;

	public FlashSaleService(RedisService redisService, FlashSaleRepository flashSaleRepository) {
		super();
		this.redisService = redisService;
		this.flashSaleRepository = flashSaleRepository;
	}

	public boolean stockValidator(long flashSaleId) {
		
		if (!redisService.lockStock(flashSaleId)) {
			return false;
		}

		return true;
	}

}
