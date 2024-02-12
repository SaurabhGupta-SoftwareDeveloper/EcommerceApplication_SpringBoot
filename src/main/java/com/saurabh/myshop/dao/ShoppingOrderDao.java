package com.saurabh.myshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saurabh.myshop.dto.ShoppingOrder;
import com.saurabh.myshop.reprositry.ShoppingOrderReprository;
@Repository
public class ShoppingOrderDao {

	@Autowired
	ShoppingOrderReprository shoppingOrderReprository;
	
	public void saveOrder(ShoppingOrder order) {
          shoppingOrderReprository.save(order);
	}

	public ShoppingOrder findOrderById(int id) {
		
		return shoppingOrderReprository.findById(id).orElseThrow();
	}
	
}
