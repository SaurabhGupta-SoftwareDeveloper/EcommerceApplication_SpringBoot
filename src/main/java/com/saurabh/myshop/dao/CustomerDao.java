package com.saurabh.myshop.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saurabh.myshop.dto.Customer;
import com.saurabh.myshop.reprositry.CustomerRepositry;

@Repository
public class CustomerDao {
        @Autowired
        CustomerRepositry repositry;
        
        public Customer save(Customer customer) {
        	return repositry.save(customer);
        }
		public boolean checkEmailDuplicate(String email) {
			
			return  repositry.existsByEmail(email);
		}
		public boolean checkMobileDuplicate(long mobile) {
			return repositry.existsByMobile(mobile);
		}
		public Customer findById(int id) {
			return repositry.findById(id).orElse(null);
		}
		public Customer findByEmail(String email) {
			return repositry.findByEmail(email);
		}
}
