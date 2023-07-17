package com.example.flashsaleproject.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.models.FlashUser;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<FlashOrder, Long> {

	Optional<FlashOrder> findByOrderNo(String orderNo);

	List<FlashOrder> findByUser(FlashUser user);

}
