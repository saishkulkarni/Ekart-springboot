package org.jsp.ekart.controller;

import java.io.IOException;
import java.util.List;

import org.jsp.ekart.dto.Product;
import org.jsp.ekart.dto.Vendor;
import org.jsp.ekart.helper.CloudinaryHelper;
import org.jsp.ekart.repository.ProductRepository;
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
	public String manageProducts(HttpSession session,ModelMap map) {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			List<Product> products=productRepository.findByVendor(vendor);
			if(products.isEmpty()) {
				session.setAttribute("failure", "No Products Present");
				return "redirect:/vendor/home";
			}else {
				map.put("products", products);
				return "vendor-view-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
}
