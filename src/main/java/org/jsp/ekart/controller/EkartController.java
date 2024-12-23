package org.jsp.ekart.controller;

import org.jsp.ekart.dto.Vendor;
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

	@GetMapping("/")
	public String loadHomePage() {
		return "home.html";
	}
	
	@GetMapping("/vendor/otp/{id}")
	public String loadOtpPage(@PathVariable int id,ModelMap map) {
		map.put("id", id);
		return "vendor-otp.html";
	}

	@GetMapping("/vendor/register")
	public String loadVendorRegistration(ModelMap map, Vendor vendor) {
		return vendorService.loadRegistration(map, vendor);
	}

	@PostMapping("/vendor/register")
	public String vendorRegistration(@Valid Vendor vendor, BindingResult result,HttpSession session) {
		return vendorService.registration(vendor, result,session);
	}
	
	@PostMapping("/vendor/otp")
	public String verifyOtp(@RequestParam int id,@RequestParam int otp,HttpSession session) {
		return vendorService.verifyOtp(id,otp,session);
	}
}
