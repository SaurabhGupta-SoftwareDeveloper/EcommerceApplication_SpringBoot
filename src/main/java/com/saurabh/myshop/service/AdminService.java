package com.saurabh.myshop.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.saurabh.myshop.dto.Product;

import jakarta.servlet.http.HttpSession;

public interface AdminService {

	String loadDashboard(HttpSession session);

	String loadAddproduct(HttpSession session, ModelMap map);

	String addproduct(Product product, BindingResult result, HttpSession session, ModelMap map, MultipartFile picture);

	String manageproducts(HttpSession session, ModelMap map);

	String deleteProduct(int id, HttpSession session);

	String editProduct(int id, HttpSession session, ModelMap map);

	String createAdmin(String email, String password, HttpSession session);

}
