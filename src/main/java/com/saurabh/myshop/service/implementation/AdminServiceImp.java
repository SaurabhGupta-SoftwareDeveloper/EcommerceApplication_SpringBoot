package com.saurabh.myshop.service.implementation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.saurabh.myshop.dao.CustomerDao;
import com.saurabh.myshop.dao.ProductDao;
import com.saurabh.myshop.dto.Customer;
import com.saurabh.myshop.dto.Product;
import com.saurabh.myshop.helper.AES;
import com.saurabh.myshop.service.AdminService;

import jakarta.servlet.http.HttpSession;
@Service
public class AdminServiceImp implements AdminService{
	@Autowired
	Product product;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	CustomerDao customerDao;

	@Override
	public String loadDashboard(HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer==null) {
			session.setAttribute("failMessage","Invalid Session");
			return "redirect:/signin";
		}else {
			if(customer.getRole().equals("ADMIN")){
				return "AdminDashboard";
			}else {
				session.setAttribute("failMessage","invalid");
				return "redirect:/";
			}
		}
	}

	

	@Override
	public String loadAddproduct(HttpSession session, ModelMap map) {
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer==null) {
			session.setAttribute("failMessage","Invalid Session");
			return "redirect:/signin";
		}else {
			if(customer.getRole().equals("ADMIN")){
				map.put("product", product);
				return "add-product";
			}else {      
				session.setAttribute("failMessage","invalid");
				return "redirect:/";
			}
		}
	}
	



	@Override
	public String addproduct(Product product, BindingResult result, HttpSession session, ModelMap map,
			MultipartFile picture) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/signin";
		} else {
			if (customer.getRole().equals("ADMIN")) {
				if (productDao.checkName(product.getName()))
					result.rejectValue("name", "error.name", "Product with Same Name Already Exists");

				if (result.hasErrors())
					return "AddProduct";
				else {
//					try {
//						byte[] image = new byte[picture.getInputStream().available()];
//						picture.getInputStream().read(image);
//
//						product.setImage(image);
					product.setImagepath("/images/" + product.getName() + ".jpg");
					productDao.save(product);

					File file = new File("src/main/resources/static/images");
					if (!file.isDirectory())
						file.mkdir();

					try {
						Files.write(Paths.get("src/main/resources/static/images", product.getName() + ".jpg"),
								picture.getBytes());
					} catch (IOException e) {
						session.setAttribute("failMessage", "You are Unauthorized to access his URL");
						return "redirect:/";
					}
					session.setAttribute("successMessage", "Product Added Success");
					return "redirect:/admin";

//					} catch (IOException e) {
//						session.setAttribute("failMessage", "You are Unauthorized to access his URL");
//						return "redirect:/";
//					}
				}
			} else {
				session.setAttribute("failMessage", "You are Unauthorized to access his URL");
				return "redirect:/";
			}
		}
			}
			    
	@Override
	public String manageproducts(HttpSession session, ModelMap map) {
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer==null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/siginin";
		}else {
			if(customer.getRole().equals("ADMIN")) {
				List<Product> products = productDao.fetchAll();
				if(products.isEmpty()) {
					session.setAttribute("failMessage", "No Products Available");
					return "redirect:/admin";
				}else{
					map.put("products", products);
					return "ManageProduct";
				}
			} else {
				session.setAttribute("failMessage", "You are Unauthorized to access his URL");
				return "redirect:/";
			}
		}
	}



	@Override
	public String deleteProduct(int id, HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer==null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/siginin";
		}else {
			if(customer.getRole().equals("ADMIN")) {
				Product product =productDao.findById(id);
				File file= new File("src/main/resources/static"+product.getImagepath());
				if(file.exists())
					file.delete();
				productDao.delete(product);
				session.setAttribute("successMessage","Product Deleted Success");
					return "redirect:/admin/manage-product";
				}
			else {
				session.setAttribute("failMessage", "You are Unauthorized to access his URL");
				return "redirect:/";
			}
		}
	}
   @Override
	public String editProduct(int id, HttpSession session, ModelMap map) {

		Customer customer=(Customer) session.getAttribute("customer");
		if(customer==null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/siginin";
		}else {
			if(customer.getRole().equals("ADMIN")) {
				Product product =productDao.findById(id);
				map.put("product", product);
				return "EditProduct";
				}
			else {
				session.setAttribute("failMessage", "You are Unauthorized to access his URL");
				return "redirect:/";
			}
		}
	}
	@Override
	public String createAdmin(String email, String password, HttpSession session) {
		Customer customer= new Customer();
		customer.setEmail(email);
		customer.setPassword(AES.encrypt( password,"123"));
		customer.setRole("ADMIN");
		customer.setVerified(true);
		customerDao.save(customer);
		session.setAttribute("successMessage", "Admin Account creation success");
		return "redirect:/";
		
				
	}

}
