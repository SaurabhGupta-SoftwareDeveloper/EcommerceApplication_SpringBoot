package com.saurabh.myshop.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.saurabh.myshop.dao.CustomerDao;
import com.saurabh.myshop.dao.ItemDao;
import com.saurabh.myshop.dao.ProductDao;
import com.saurabh.myshop.dao.ShoppingOrderDao;
import com.saurabh.myshop.dto.Cart;
import com.saurabh.myshop.dto.Customer;
import com.saurabh.myshop.dto.Item;
import com.saurabh.myshop.dto.Product;
import com.saurabh.myshop.dto.ShoppingOrder;
import com.saurabh.myshop.helper.AES;
import com.saurabh.myshop.service.CustomerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Service
public class CustomerServiceimpl implements CustomerService {
	
	
	@Autowired
	ShoppingOrderDao orderDao;
           
	@Autowired
	CustomerDao dao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	MailSender mailSender;
	 
	@Autowired
	ItemDao itemDao;

	@Override
	public String save(@Valid Customer customer, BindingResult result) {
		
			if(dao.checkEmailDuplicate(customer.getEmail()))
				result.rejectValue("email", "error.email", "this email already used");
			if(dao.checkMobileDuplicate(customer.getMobile()))
				result.rejectValue("mobile", "error.mobile", "this mobile already used");
			if(result.hasErrors()) {
				return "Signup";
			}else {
				customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
				customer.setRole("USER");
				dao.save(customer);
				return "redirect:/send-otp/"+customer.getId()
				
				;
			}
		}

	@Override
	public String verifyOtp(int id, int otp, ModelMap map) {
		Customer customer = dao.findById(id);
		System.out.println("*******2********");
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
			dao.save(customer);
			return "redirect:/signin";
		} else {
			map.put("failMessage", "Invalid Otp, Try Again!");
			map.put("id", id);
			return "VerifyOtp";
		}
	}

	@Override
	public String sendOtp(int id, ModelMap map) {
		Customer customer=dao.findById(id);
		customer.setOtp(new Random().nextInt(100000, 999999));
		dao.save(customer);
		//Logic for Sending Otp
		map.put("id", id);
		map.put("successMessage", "Otp Sent Success");
		return "VerifyOtp";
	}

	@Override
	public String resendOtp(int id, ModelMap map) {
		Customer customer=dao.findById(id);
		customer.setOtp(new Random().nextInt(100000, 999999));
		dao.save(customer);
		//Logic for Resending Otp
		map.put("id", id);
		map.put("successMessage", "Otp Resent Success");
		return "VerifyOtp";
	}

	@Override
	public String login(String email, String password, ModelMap map, HttpSession session) {
		Customer customer = dao.findByEmail(email);
		if (customer == null)
			session.setAttribute("failMessage", "Invalid Email");
		else {
			if (AES.decrypt(customer.getPassword(), "123").equals(password)) {
				if (customer.isVerified()) {
					session.setAttribute("customer", customer);
					session.setAttribute("successMessage", "Login Success");
					return "redirect:/";
				} else {
					return resendOtp(customer.getId(), map);
				}
			} else
				session.setAttribute("failMessage", "Invalid Password");
		}
		return "Login";
	}

	@Override
	public String viewProducts(HttpSession session, ModelMap map) {
		List<Product> products = productDao.fetchAll();
		if (products.isEmpty()) {
			session.setAttribute("failMessage", "No Products Present");
			return "redirect:/";
		} else {
			map.put("products", products);
			return "ViewProduct";
		}
	}

	@Override
	public String viewCart(HttpSession session, ModelMap map) {
		   Customer customer=(Customer) session.getAttribute("customer");
		   if(customer==null) {
			   session.setAttribute("failMessage","Invalid session");
			   return "redirect:/signin";
		   }else {
			       Cart cart= customer.getCart();
			        List<Item>items=cart.getItems();
			        if(items.isEmpty()) {
			        	session.setAttribute("failMessage", "No items available");
			        	return "redirect:/";
			        }else {
			        	map.put("items", items);
			        	return "ViewCart";
			        }
		   }
	}

	@Override
	public String addToCart(int id, HttpSession session) {
		  Customer customer=(Customer) session.getAttribute("customer");
		   if(customer==null) {
			   session.setAttribute("failMessage","Invalid session");
			   return "redirect:/signin";
		   }else {
			   Product product= productDao.findById(id);
			   if(product.getStock()>0) {
				  Cart cart=customer.getCart();
				  List<Item> items=cart.getItems();
				  if(items.isEmpty()) {
					  Item item = new Item();
					  item.setCategory(product.getCategory());
					  item.setDescription(product.getDescription());
					  item.setImagePath(product.getImagepath());
					  item.setName(product.getName());
					  item.setPrice(product.getPrice());
					  item.setQuantity(1);
					  items.add(item);
					  session.setAttribute("successMessage","Item added to cart success");
				  }else {
					  boolean flag=true;
					  for(Item item: items) {
						  if(item.getName().equals(product.getName())) {
							  flag = false;
								if (item.getQuantity() < product.getStock()) {
									item.setQuantity(item.getQuantity() + 1);
									item.setPrice(item.getPrice() + product.getPrice());
									session.setAttribute("successMessage", "Item added to Cart Success");
								} else {
									session.setAttribute("failMessage", "Out of Stock");
								}
								break;
							}
						}
					  if (flag) {
							Item item = new Item();
							item.setCategory(product.getCategory());
							item.setDescription(product.getDescription());
							item.setImagePath(product.getImagepath());
							item.setName(product.getName());
							item.setPrice(product.getPrice());
							item.setQuantity(1);
							items.add(item);
							session.setAttribute("successMessage", "Item added to Cart Success");
						  }
					  }
					dao.save(customer);
					session.setAttribute("customer", dao.findById(customer.getId()));
					return "redirect:/products";
				  }else {
					  session.setAttribute("failMessage", "Out of Stock");
						return "redirect:/";
				  }
			   }
	}

	@Override
	public String paymentPage(HttpSession session, ModelMap map) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/signin";
		} else {
			    List<Item> items= customer.getCart().getItems();
			    if (items.isEmpty()) {
					session.setAttribute("failMessage", "Nothing to Buy");
					return "redirect:/";
				}
			    double price=items.stream().mapToDouble(x-> x.getPrice()).sum();
			    try {
			    	RazorpayClient razorpay = new RazorpayClient("rzp_test_NL2VDewmKxugHZ", "OYO8g3i8aCPLCfn8piTOYlbi");

					JSONObject orderRequest = new JSONObject();
					orderRequest.put("amount", price * 100);
					orderRequest.put("currency", "INR");

					Order order = razorpay.orders.create(orderRequest);

					ShoppingOrder myOrder = new ShoppingOrder();
					myOrder.setDateTime(LocalDateTime.now());
					myOrder.setItems(items);
					myOrder.setOrderId(order.get("id"));
					myOrder.setStatus(order.get("status"));
					myOrder.setTotalPrice(price);

					orderDao.saveOrder(myOrder);

					map.put("key", "rzp_test_NL2VDewmKxugHZ");
					map.put("myOrder", myOrder);
					map.put("customer", customer);

					customer.getOrders().add(myOrder);
					dao.save(customer);
					session.setAttribute("customer", dao.findById(customer.getId()));
					return "PaymentPage";

				} catch (RazorpayException e) {
					e.printStackTrace();
					return "redirect:/";
				}
		}
	}

	@Override
	public String removeFromCart(int id, HttpSession session) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/signin";
		} else {
			Item item = itemDao.findById(id);
			if (item.getQuantity() == 1) {
				customer.getCart().getItems().remove(item);
				dao.save(customer);
				session.setAttribute("customer", dao.findById(customer.getId()));
				itemDao.delete(item);
				session.setAttribute("successMessage", "Item Removed from Cart");

			} else {
				item.setPrice(item.getPrice() - (item.getPrice() / item.getQuantity()));
				item.setQuantity(item.getQuantity() - 1);
				itemDao.save(item);
				session.setAttribute("successMessage", "Item Quantity Reduced By 1");
			}
			session.setAttribute("customer", dao.findById(customer.getId()));
			return "redirect:/cart";
		}
	}

	@Override
	public String confirmOrder(HttpSession session, int id, String razorpay_payment_id) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/signin";
		} else {
			for (Item item : customer.getCart().getItems()) {
				Product product = productDao.findByName(item.getName());
				product.setStock(product.getStock() - item.getQuantity());
				productDao.save(product);
			}
			ShoppingOrder order = orderDao.findOrderById(id);
			order.setPaymnetId(razorpay_payment_id);
			order.setStatus("success");
			orderDao.saveOrder(order);
			customer.getCart().setItems(new ArrayList<Item>());
			dao.save(customer);
			session.setAttribute("customer", dao.findById(customer.getId()));
			session.setAttribute("successMessage", "Order Placed Success");
			return "redirect:/";
		}
	}

	@Override
	public String viewOrders(HttpSession session, ModelMap map) {
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer == null) {
			session.setAttribute("failMessage", "Invalid Session");
			return "redirect:/signin";
		} else {
			List<ShoppingOrder> orders = customer.getOrders();
			if (orders == null || orders.isEmpty()) {
				session.setAttribute("failMessage", "No Orders Yet");
				return "redirect:/";
			} else {
				map.put("orders", orders);
				return "ViewOrders";
			}
		}
	}
	}
	

