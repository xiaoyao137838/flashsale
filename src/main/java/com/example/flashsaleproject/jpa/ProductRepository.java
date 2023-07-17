package com.example.flashsaleproject.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flashsaleproject.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
