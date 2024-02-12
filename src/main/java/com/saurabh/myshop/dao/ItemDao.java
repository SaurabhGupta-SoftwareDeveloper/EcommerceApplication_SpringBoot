package com.saurabh.myshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saurabh.myshop.dto.Item;
import com.saurabh.myshop.service.ItemRepositry;

@Repository
public class ItemDao {
   @Autowired
   ItemRepositry itemRepositry;

public Item findById(int id) {
	
	return itemRepositry.findById(id).orElseThrow();
			
}

public void delete(Item item) {
	  itemRepositry.delete(item);
	
}

public void save(Item item) {
	itemRepositry.save(item);
}
   
}
