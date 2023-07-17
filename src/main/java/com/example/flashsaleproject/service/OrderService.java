package com.example.flashsaleproject.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.jpa.OrderRepository;
import com.example.flashsaleproject.jpa.UserRepository;
import com.example.flashsaleproject.kafka.KafkaProducer;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.models.FlashUser;
import com.example.flashsaleproject.redis.RedisService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private OrderRepository orderRepository;
	
	private FlashSaleRepository flashSaleRepository;
	
	private UserRepository userRepository;
	
	private KafkaProducer kafkaProducer;

	public OrderService(OrderRepository orderRepository, FlashSaleRepository flashSaleRepository,
			UserRepository userRepository, KafkaProducer kafkaProducer) {
		super();
		this.orderRepository = orderRepository;
		this.flashSaleRepository = flashSaleRepository;
		this.userRepository = userRepository;
		this.kafkaProducer = kafkaProducer;
	}

	public FlashOrder createOrder(long flashSaleId, long userId) throws Exception {
		
		Optional<FlashSale> optionalFlashSale = flashSaleRepository.findById(flashSaleId);
		Optional<FlashUser> optionalUser = userRepository.findById(userId);
		
		if (optionalFlashSale.isEmpty()) {
			throw new Exception("This flash sale does not exist.");
		}
		if (optionalUser.isEmpty()) {
			throw new Exception("This user does not exist.");
		}
		
		String orderNo = UUID.randomUUID().toString();
		int status = 0;
		FlashSale flashSale = optionalFlashSale.get();
		BigDecimal amount = flashSale.getNewPrice();
		FlashUser user = optionalUser.get();
		Date createdAt = new Date();
		
		FlashOrder order = new FlashOrder();
		order.setOrderNo(orderNo);
		order.setStatus(status);
		order.setAmount(amount);
		order.setCreatedAt(createdAt);
		order.setFlashSale(flashSale);
		order.setUser(user);

		kafkaProducer.createOrder(order);
		
		return order;
	}
	
	public void payOrderProcess(String orderNo) throws Exception {
		payOrderProcess(orderNo, 1);
	}
	
	public void payOrderProcess(String orderNo, long userId) throws Exception {
		
		Optional<FlashOrder> optionalOrder = orderRepository.findByOrderNo(orderNo);
		if (optionalOrder.isEmpty()) {
			logger.error("The order does not exist: {}", orderNo);
			throw new Exception("This order does not exist.");
		}
		
		FlashOrder order = optionalOrder.get();
		if (order.getStatus() != 0) {
			logger.error("The order in not valid to pay.");
			throw new Exception("This order is not valid to pay.");
		}
		
		if (order.getUser().getId() != userId) {
			logger.error("The current user does not have the authority to process the order.");
			throw new Exception("You are not allowed to pay for this order.");
		}
		
		kafkaProducer.payDone(order);
	}

}
