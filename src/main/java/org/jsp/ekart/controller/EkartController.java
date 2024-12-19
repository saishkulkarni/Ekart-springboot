package org.jsp.ekart.controller;

import org.jsp.ekart.dto.Vendor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class EkartController {
	@GetMapping
	public String loadHomePage() {
		return "home.html";
	}

	@GetMapping("/vendor/register")
	public String loadVendorRegistration(ModelMap map,Vendor vendor) {
		map.put("vendor", vendor);
		return "vendor-register.html";
	}

	@PostMapping("/vendor/register")
	public String vendorRegistration(@Valid Vendor vendor,BindingResult result) {
		if(result.hasErrors())
			return "vendor-register.html";
		else {
		System.err.println(vendor);	
		return "redirect:https://www.youtube.com";
		}
	}
}
