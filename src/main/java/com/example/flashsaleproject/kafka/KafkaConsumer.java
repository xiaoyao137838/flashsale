package com.example.flashsaleproject.kafka;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.jpa.OrderRepository;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.redis.RedisService;
import com.example.flashsaleproject.utils.OrderConverter;
import com.example.flashsaleproject.utils.OrderVM;

@Service
public class KafkaConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	
	OrderRepository orderRepository;
	
	FlashSaleRepository flashSaleRepository;
	
	RedisService redisService;
		
	public KafkaConsumer(OrderRepository orderRepository, FlashSaleRepository flashSaleRepository, RedisService redisService) {
		super();
		this.orderRepository = orderRepository;
		this.flashSaleRepository = flashSaleRepository;
		this.redisService = redisService;
	}
	
	@KafkaListener(topics = "${spring.kafka.topic-order-creation.name}",
			groupId = "${spring.kafka.consumer.group-id}")
	private void createOrder(OrderVM orderVM) {
		
		logger.info("Order is created by Kafka consumer");
		
		FlashOrder order = OrderConverter.convertOrderVM(orderVM);
		
		orderRepository.save(order);
		
		logger.info("Consumer -> createOrder: {}", order.toString());
		
		FlashSale flashSale = order.getFlashSale();
		flashSale.setLockedQuantity(flashSale.getLockedQuantity() + 1);
		flashSale.setAvailableQuantity(flashSale.getAvailableQuantity() - 1);
		flashSaleRepository.save(flashSale);
	}

	@KafkaListener(topics = "${spring.kafka.topic-pay-done.name}",
			groupId = "${spring.kafka.consumer.group-id}")
	private void payDone(OrderVM orderVM) {
		
		logger.info("Order is updated by Kafka consumer after payment");
		
		FlashOrder order = OrderConverter.convertOrderVM(orderVM);
		
		order.setPaiedAt(new Date());
		order.setStatus(2);
		orderRepository.save(order);
		
		logger.info("Consumer -> payDoneOrder: {}", order.toString());
		
		FlashSale flashSale = order.getFlashSale();
		flashSale.setLockedQuantity(flashSale.getLockedQuantity() - 1);
		flashSaleRepository.save(flashSale);
		
		redisService.removeLimitMember(flashSale.getId(), order.getUser().getId());
	}

}
