package org.jsp.ekart.controller;

import java.io.IOException;
import java.util.List;

import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.dto.Product;
import org.jsp.ekart.dto.Vendor;
import org.jsp.ekart.helper.CloudinaryHelper;
import org.jsp.ekart.repository.ProductRepository;
import org.jsp.ekart.service.CustomerService;
import org.jsp.ekart.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${admin.email}")
	String adminEmail;
	@Value("${admin.password}")
	String adminPassword;

	@Autowired
	VendorService vendorService;

	@Autowired
	CustomerService customerService;

	@Autowired
	CloudinaryHelper cloudinaryHelper;

	@Autowired
	ProductRepository productRepository;

	@GetMapping("/")
	public String loadHomePage() {
		return "home.html";
	}

	@GetMapping("/vendor/otp/{id}")
	public String loadOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "vendor-otp.html";
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
		if (session.getAttribute("vendor") != null)
			return "vendor-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("vendor");
		session.setAttribute("success", "Logged out Success");
		return "redirect:/";
	}

	@GetMapping("/add-product")
	public String loadAddProduct(HttpSession session) {
		if (session.getAttribute("vendor") != null)
			return "add-product.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	@PostMapping("/add-product")
	public String addProduct(Product product, HttpSession session) throws IOException {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			product.setVendor(vendor);
			product.setImageLink(cloudinaryHelper.saveToCloudinary(product.getImage()));
			productRepository.save(product);
			session.setAttribute("success", "Product Added Success");
			return "redirect:/vendor/home";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	@GetMapping("/manage-products")
	public String manageProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			List<Product> products = productRepository.findByVendor(vendor);
			if (products.isEmpty()) {
				session.setAttribute("failure", "No Products Present");
				return "redirect:/vendor/home";
			} else {
				map.put("products", products);
				return "vendor-view-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id, HttpSession session) {
		if (session.getAttribute("vendor") != null) {
			productRepository.deleteById(id);
			session.setAttribute("success", "Product Deleted Success");
			return "redirect:/manage-products";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	@GetMapping("/admin/login")
	public String loadAdminLogin() {
		return "admin-login.html";
	}

	@PostMapping("/admin/login")
	public String adminLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {

		if (email.equals(adminEmail)) {
			if (password.equals(adminPassword)) {
				session.setAttribute("admin", adminEmail);
				session.setAttribute("success", "Login Success as Admin");
				return "redirect:/admin/home";
			} else {
				session.setAttribute("failure", "Invalid Password");
				return "redirect:/admin/login";
			}
		} else {
			session.setAttribute("failure", "Invalid Email");
			return "redirect:/admin/login";
		}
	}

	@GetMapping("/admin/home")
	public String loadAdminHome(HttpSession session) {
		if (session.getAttribute("admin") != null)
			return "admin-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/admin/login";
		}
	}

	@GetMapping("/approve-products")
	public String approveProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				session.setAttribute("failure", "No Products Present");
				return "redirect:/admin/home";
			} else {
				map.put("products", products);
				return "admin-view-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/admin/login";
		}
	}

	@GetMapping("/change/{id}")
	public String changeStatus(@PathVariable int id, HttpSession session) {
		if (session.getAttribute("admin") != null) {
			Product product = productRepository.findById(id).get();
			if (product.isApproved())
				product.setApproved(false);
			else
				product.setApproved(true);

			productRepository.save(product);
			session.setAttribute("success", "Product Status Changed Success");
			return "redirect:/approve-products";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/admin/login";
		}
	}

	@GetMapping("/customer/otp/{id}")
	public String loadCustomerOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "customer-otp.html";
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
		if (session.getAttribute("customer") != null)
			return "customer-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}
}
