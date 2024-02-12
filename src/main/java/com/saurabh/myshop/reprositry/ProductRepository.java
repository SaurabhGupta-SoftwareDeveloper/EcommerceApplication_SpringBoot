package com.saurabh.myshop.reprositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.myshop.dto.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {



	boolean existsByName(String name);

	Product findByName(String name);



}
