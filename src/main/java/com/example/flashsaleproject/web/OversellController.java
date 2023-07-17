package com.example.flashsaleproject.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.flashsaleproject.service.FlashSaleService;
import com.example.flashsaleproject.service.OversellService;

@Controller
public class OversellController {
	
	private OversellService oversellService;
	
	private FlashSaleService flashSaleService;

	public OversellController(OversellService oversellService, FlashSaleService flashSaleService) {
		super();
		this.oversellService = oversellService;
		this.flashSaleService = flashSaleService;
	}
	
	@GetMapping("/hello") 
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("Hello world");
	}
	
	@GetMapping("/oversell/valid/{flashSaleId}")
	public ResponseEntity<String> validMethod(@PathVariable long flashSaleId) {
		if (flashSaleService.stockValidator(flashSaleId)) {
			return ResponseEntity.ok("Purchased success");
		}
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/oversell/redis/{flashSaleId}")
	public ResponseEntity<String> redisMethod(@PathVariable long flashSaleId) {
		if (oversellService.stockValidatorRedis(flashSaleId)) {
			return ResponseEntity.ok("Purchased success");
		}
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/oversell/db/{flashSaleId}")
	public ResponseEntity<String> dbMethod(@PathVariable long flashSaleId) {
		if (oversellService.stockValidatorDB(flashSaleId)) {
			return ResponseEntity.ok("Purchased success");
		}
		return ResponseEntity.badRequest().build();
	}
}
