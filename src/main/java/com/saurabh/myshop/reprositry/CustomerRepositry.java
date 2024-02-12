package com.saurabh.myshop.reprositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.myshop.dto.Customer;

public interface CustomerRepositry  extends JpaRepository<Customer, Integer>{

	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	Customer findByEmail(String email);

}
