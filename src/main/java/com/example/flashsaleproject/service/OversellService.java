package com.example.flashsaleproject.service;

import org.springframework.stereotype.Service;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.redis.RedisService;

@Service
public class OversellService {
	
	private RedisService redisService;
	
	private FlashSaleRepository flashSaleRepository;

	public OversellService(RedisService redisService, FlashSaleRepository flashSaleRepository) {
		super();
		this.redisService = redisService;
		this.flashSaleRepository = flashSaleRepository;
	}
	
	public boolean stockValidatorDB(long flashSaleId) {
		FlashSale flashSale = flashSaleRepository.findById(flashSaleId).get();
		if (flashSale.getAvailableQuantity() > 0) {
			flashSale.setAvailableQuantity(flashSale.getAvailableQuantity() - 1);
			flashSale.setLockedQuantity(flashSale.getLockedQuantity() + 1);
			flashSaleRepository.save(flashSale);
			return true;
		}
		return false;
	}
	
	public boolean stockValidatorRedis(long flashSaleId) {
		String countStr = redisService.getOversellQuantity(flashSaleId);
		int count = Integer.parseInt(countStr);
		
		if (count > 0) {
			redisService.addOversellQuantity(flashSaleId, count - 1);
			return true;
		}
		return false;
	}

}
