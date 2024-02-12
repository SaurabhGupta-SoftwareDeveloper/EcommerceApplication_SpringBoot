package com.saurabh.myshop.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.myshop.dto.Item;

public interface ItemRepositry extends JpaRepository<Item, Integer>{

}
