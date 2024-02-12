package com.saurabh.myshop.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saurabh.myshop.dto.Product;
import com.saurabh.myshop.reprositry.ProductRepository;

@Repository
public class ProductDao {

	@Autowired
	ProductRepository productRepository;
	
	public boolean checkName(String name) {
		return productRepository.existsByName(name);
	}

	public List<Product> fetchAll() {
		return productRepository.findAll();
	}

	public void save(Product product) {
		 productRepository.save(product);
		
	}

	public Product findById(int id) {
		return productRepository.findById(id).orElse(null);
	}

	public void delete(Product product) {
		productRepository.delete(product);
		
	}

	public Product findByName(String name) {
		// TODO Auto-generated method stub
		return  productRepository.findByName(name);
	}
}
