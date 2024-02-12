package com.saurabh.myshop.reprositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.myshop.dto.ShoppingOrder;

public interface ShoppingOrderReprository extends JpaRepository<ShoppingOrder, Integer> {

}
