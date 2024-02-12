package com.saurabh.myshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.saurabh.myshop.dto.Product;
import com.saurabh.myshop.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminService adminService;
	
	@GetMapping
	public String loadDashboard(HttpSession session) {
		return adminService.loadDashboard(session);
	}
	@GetMapping("/add-product")
	public String loadAddProduct(HttpSession session, ModelMap map) {
		return adminService.loadAddproduct(session ,map);
	}
	@GetMapping("/manage-product")
	public String loadManageProduct(HttpSession session,ModelMap map) {
		return adminService.manageproducts(session,map);
	}
	@PostMapping("/add-product")
	public String AddProduct(Product product,BindingResult result,MultipartFile  picture,  HttpSession session, ModelMap map) {
		return adminService.addproduct(product,result,session,map ,picture);
	}
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		return adminService.deleteProduct(id,session);
	}
	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, HttpSession session,ModelMap map) {
		return adminService.editProduct(id,session,map);
	}
	@GetMapping("/create-admin/{email}/{password}")
	public String createAdmin(@PathVariable String email,@PathVariable String password, HttpSession session) {
		return adminService.createAdmin(email,password,session);
	}
	
}
