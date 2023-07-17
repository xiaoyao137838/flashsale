package com.example.flashsaleproject.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic-order-creation.name}")
    private String orderCreationTopic;
    
    @Value("${spring.kafka.topic-pay-done.name}")
	private String payDoneTopic;

    @Bean
    public NewTopic orderCreationTopic(){
        return TopicBuilder.name(orderCreationTopic)
                .build();
    }
    
    @Bean
    public NewTopic payDoneTopic(){
        return TopicBuilder.name(payDoneTopic)
                .build();
    }
}
