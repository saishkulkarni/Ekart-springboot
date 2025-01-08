package org.jsp.ekart.controller;

import java.io.IOException;

import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.dto.Product;
import org.jsp.ekart.dto.Vendor;
import org.jsp.ekart.service.AdminService;
import org.jsp.ekart.service.CustomerService;
import org.jsp.ekart.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class EkartController {

	@Autowired
	VendorService vendorService;

	@Autowired
	CustomerService customerService;

	@Autowired
	AdminService adminService;

	@GetMapping("/")
	public String loadHomePage() {
		return "home.html";
	}

	@GetMapping("/vendor/otp/{id}")
	public String loadOtpPage(@PathVariable int id, ModelMap map) {
		return vendorService.loadOtpPage(id, map);
	}

	@GetMapping("/vendor/register")
	public String loadVendorRegistration(ModelMap map, Vendor vendor) {
		return vendorService.loadRegistration(map, vendor);
	}

	@PostMapping("/vendor/register")
	public String vendorRegistration(@Valid Vendor vendor, BindingResult result, HttpSession session) {
		return vendorService.registration(vendor, result, session);
	}

	@PostMapping("/vendor/otp")
	public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return vendorService.verifyOtp(id, otp, session);
	}

	@GetMapping("/vendor/login")
	public String loadLogin() {
		return "vendor-login.html";
	}

	@PostMapping("/vendor/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return vendorService.login(email, password, session);
	}

	@GetMapping("/vendor/home")
	public String loadHome(HttpSession session) {
		return vendorService.loadHome(session);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return adminService.logout(session);
	}

	@GetMapping("/add-product")
	public String loadAddProduct(HttpSession session) {
		return vendorService.addProduct(session);
	}

	@PostMapping("/add-product")
	public String addProduct(Product product, HttpSession session) throws IOException {
		return vendorService.addProduct(product, session);
	}

	@GetMapping("/manage-products")
	public String manageProducts(HttpSession session, ModelMap map) {
		return vendorService.manageProducts(session, map);
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id, HttpSession session) {
		return vendorService.deleteProduct(id, session);
	}

	@GetMapping("/admin/login")
	public String loadAdminLogin() {
		return "admin-login.html";
	}

	@PostMapping("/admin/login")
	public String adminLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return adminService.login(email, password, session);
	}

	@GetMapping("/admin/home")
	public String loadAdminHome(HttpSession session) {
		return adminService.loadHome(session);
	}

	@GetMapping("/approve-products")
	public String approveProducts(HttpSession session, ModelMap map) {
		return adminService.approveProducts(session, map);
	}

	@GetMapping("/change/{id}")
	public String changeStatus(@PathVariable int id, HttpSession session) {
		return adminService.changeStatus(id, session);
	}

	@GetMapping("/customer/otp/{id}")
	public String loadCustomerOtpPage(@PathVariable int id, ModelMap map) {
		return customerService.loadCustomerOtp(id, map);
	}

	@GetMapping("/customer/register")
	public String loadCustomerRegistration(ModelMap map, Customer customer) {
		return customerService.loadRegistration(map, customer);
	}

	@PostMapping("/customer/register")
	public String customerRegistration(@Valid Customer customer, BindingResult result, HttpSession session) {
		return customerService.registration(customer, result, session);
	}

	@PostMapping("/customer/otp")
	public String verifyCustomerOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return customerService.verifyOtp(id, otp, session);
	}

	@GetMapping("/customer/login")
	public String loadCustomerLogin() {
		return "customer-login.html";
	}

	@PostMapping("/customer/login")
	public String customerLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return customerService.login(email, password, session);
	}

	@GetMapping("/customer/home")
	public String loadCustomerHome(HttpSession session) {
		return customerService.loadHome(session);
	}

	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, ModelMap map, HttpSession session) {
		return vendorService.editProdcut(id, map, session);
	}

	@PostMapping("/update-product")
	public String updateProduct(Product product, HttpSession session) throws IOException {
		return vendorService.updateProduct(product, session);
	}

	@GetMapping("/view-products")
	public String viewProducts(HttpSession session, ModelMap map) {
		return customerService.viewProducts(session, map);
	}

	@GetMapping("/search-products")
	public String searchProducts(HttpSession session) {
		return customerService.searchProducts(session);
	}

	@PostMapping("/search-products")
	public String search(@RequestParam String query, HttpSession session, ModelMap map) {
		return customerService.searchProducts(session, query, map);
	}

	@GetMapping("/view-cart")
	public String viewCart(HttpSession session, ModelMap map) {
		return customerService.viewCart(session, map);
	}

	@GetMapping("/add-cart/{id}")
	public String addToCart(@PathVariable int id, HttpSession session) {
		return customerService.addToCart(id, session);
	}

	@GetMapping("/increase/{id}")
	public String increase(@PathVariable int id, HttpSession session) {
		return customerService.increase(id, session);
	}

	@GetMapping("/decrease/{id}")
	public String decrease(@PathVariable int id, HttpSession session) {
		return customerService.decrease(id, session);
	}

	@GetMapping("/payment")
	public String payment(HttpSession session, ModelMap map) {
		return customerService.payment(session, map);
	}

	@PostMapping("/success")
	public String paymentSuccess(org.jsp.ekart.dto.Order order, HttpSession session) {
		return customerService.paymentSuccess(order, session);
	}

	@GetMapping("/view-orders")
	public String viewOrders(HttpSession session, ModelMap map) {
		return customerService.viewOrders(session, map);
	}
}