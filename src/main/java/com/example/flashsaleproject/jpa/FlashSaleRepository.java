package com.example.flashsaleproject.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flashsaleproject.models.FlashSale;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

}
