package com.example.flashsaleproject.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.utils.OrderConverter;
import com.example.flashsaleproject.utils.OrderVM;

@Service
public class KafkaProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
	
	@Value("${spring.kafka.topic-order-creation.name}")
	private String orderCreationTopic;
	
	@Value("${spring.kafka.topic-pay-done.name}")
	private String payDoneTopic;
	
	private KafkaTemplate<String, OrderVM> kafkaTemplate;

	public KafkaProducer(KafkaTemplate<String, OrderVM> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void createOrder(FlashOrder order) {
		
		logger.info("The order is sent by Kafka to create on topic: {}", orderCreationTopic);
		
		OrderVM orderVM = OrderConverter.convertFlashOrder(order);
		
		logger.info("Producer -> CreatedOrderVM: {}", orderVM);
		
		kafkaTemplate.send(orderCreationTopic, orderVM);
	}

	public void payDone(FlashOrder order) {
		
		logger.info("The order is sent by Kafka after payment on topic: {}", payDoneTopic);
		
		OrderVM orderVM = OrderConverter.convertFlashOrder(order);
		
		logger.info("Producer -> payDoneOrderVM: {}", orderVM);
		
		kafkaTemplate.send(payDoneTopic, orderVM);
	}

}
