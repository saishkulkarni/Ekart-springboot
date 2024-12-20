package org.jsp.ekart.service;

import java.util.Random;

import org.jsp.ekart.dto.Vendor;
import org.jsp.ekart.helper.AES;
import org.jsp.ekart.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;

@Service
public class VendorService {

	@Autowired
	VendorRepository vendorRepository;

	public String loadRegistration(ModelMap map, Vendor vendor) {
		map.put("vendor", vendor);
		return "vendor-register.html";
	}

	public String registration(@Valid Vendor vendor, BindingResult result) {
		if (!vendor.getPassword().equals(vendor.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Confirm Password Should Match");
		if (vendorRepository.existsByEmail(vendor.getEmail()))
			result.rejectValue("email", "error.email", "* Email Already Exists");
		if (vendorRepository.existsByMobile(vendor.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Already Exists");

		if (result.hasErrors())
			return "vendor-register.html";
		else {
			int otp=new Random().nextInt(100000, 1000000);
			vendor.setOtp(otp);
			vendor.setPassword(AES.encrypt(vendor.getPassword()));
			vendorRepository.save(vendor);
			//email Logic
			System.err.println(vendor.getOtp());
			
			return "redirect:/vendor/otp/"+vendor.getId();
		}
	}

	public String verifyOtp(int id, int otp) {
		Vendor vendor=vendorRepository.findById(id).orElseThrow();
		if(vendor.getOtp()==otp) {
			vendor.setVerified(true);
			vendorRepository.save(vendor);
			return "redirect:/";
		}else {
			return "redirect:/vendor/otp/"+vendor.getId();
		}
	}

}
