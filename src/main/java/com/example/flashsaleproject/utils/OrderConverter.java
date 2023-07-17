package com.example.flashsaleproject.utils;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.jpa.UserRepository;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.models.FlashUser;

@Component
public class OrderConverter {
	
	private static FlashSaleRepository flashSaleRepository;
	
	private static UserRepository userRepository;

	public OrderConverter(FlashSaleRepository flashSaleRepository, UserRepository userRepository) {
		super();
		OrderConverter.flashSaleRepository = flashSaleRepository;
		OrderConverter.userRepository = userRepository;
	}
	
	public static OrderVM convertFlashOrder(FlashOrder order) {
		OrderVM orderVM = new OrderVM();
		orderVM.setId(order.getId());
		orderVM.setOrderNo(order.getOrderNo());
		orderVM.setStatus(order.getStatus());
		orderVM.setFlashSaleId(order.getFlashSale().getId());
		orderVM.setAmount(order.getAmount());
		orderVM.setUserId(order.getUser().getId());
		orderVM.setCreatedAt(order.getCreatedAt().getTime());
		orderVM.setPaiedAt(order.getPaiedAt() != null ? order.getPaiedAt().getTime() : 0L);
		
		return orderVM;
	}
	
	public static FlashOrder convertOrderVM(OrderVM orderVM) {
		FlashSale flashSale = flashSaleRepository.findById(orderVM.getFlashSaleId()).get();
		FlashUser user = userRepository.findById(orderVM.getUserId()).get();
		Long vmId = orderVM.getId();
		Long id = vmId != null && vmId > 0 ? vmId : null;
		
		FlashOrder order = new FlashOrder();
		order.setId(id);
		order.setOrderNo(orderVM.getOrderNo());
		order.setStatus(orderVM.getStatus());
		order.setFlashSale(flashSale);
		order.setAmount(orderVM.getAmount());
		order.setUser(user);
		order.setCreatedAt(new Date(orderVM.getCreatedAt()));
		order.setPaiedAt(new Date(orderVM.getPaiedAt()));
		
		return order;
	}

}
