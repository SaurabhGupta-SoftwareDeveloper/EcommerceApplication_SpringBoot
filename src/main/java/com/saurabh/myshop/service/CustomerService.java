package com.saurabh.myshop.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.saurabh.myshop.dto.Customer;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface CustomerService {

	String save(@Valid Customer customer, BindingResult result);

	String sendOtp(int id, ModelMap map);

	String verifyOtp(int id, int otp, ModelMap map);

	String resendOtp(int id, ModelMap map);

	String login(String email, String password, ModelMap map, HttpSession session);

	String viewProducts(HttpSession session, ModelMap map);

	String viewCart(HttpSession session, ModelMap map);

	String addToCart(int id, HttpSession session);

	String paymentPage(HttpSession session, ModelMap map);

	String removeFromCart(int id, HttpSession session);

	String confirmOrder(HttpSession session, int id, String razorpay_payment_id);

	String viewOrders(HttpSession session, ModelMap map);

}
