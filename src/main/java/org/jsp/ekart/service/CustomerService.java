package org.jsp.ekart.service;

import java.util.Random;

import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.helper.AES;
import org.jsp.ekart.helper.EmailSender;
import org.jsp.ekart.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EmailSender emailSender;

	public String loadRegistration(ModelMap map, Customer customer) {
		map.put("customer", customer);
		return "customer-register.html";
	}

	public String registration(Customer customer, BindingResult result, HttpSession session) {
		if (!customer.getPassword().equals(customer.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Confirm Password Should Match");
		if (customerRepository.existsByEmail(customer.getEmail()))
			result.rejectValue("email", "error.email", "* Email Already Exists");
		if (customerRepository.existsByMobile(customer.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Already Exists");

		if (result.hasErrors())
			return "customer-register.html";
		else {
			int otp = new Random().nextInt(100000, 1000000);
			customer.setOtp(otp);
			customer.setPassword(AES.encrypt(customer.getPassword()));
			customerRepository.save(customer);
			// emailSender.send(customer);
			System.err.println(customer.getOtp());
			session.setAttribute("success", "Otp Sent Successfully");
			return "redirect:/customer/otp/" + customer.getId();
		}
	}

	public String verifyOtp(int id, int otp, HttpSession session) {
		Customer customer = customerRepository.findById(id).orElseThrow();
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
			customerRepository.save(customer);
			session.setAttribute("success", "Customer Account Created Success");
			return "redirect:/";
		} else {
			session.setAttribute("failure", "OTP Missmatch");
			return "redirect:/customer/otp/" + customer.getId();
		}
	}

	public String login(String email, String password, HttpSession session) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer == null) {
			session.setAttribute("failure", "Invalid Email");
			return "redirect:/customer/login";
		} else {
			if (AES.decrypt(customer.getPassword()).equals(password)) {
				if (customer.isVerified()) {
					session.setAttribute("customer", customer);
					session.setAttribute("success", "Login Success");
					return "redirect:/customer/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					customer.setOtp(otp);
					customerRepository.save(customer);
					// emailSender.send(customer);
					System.err.println(customer.getOtp());
					session.setAttribute("success", "Otp Sent Successfully, First Verify Email for Logging In");
					return "redirect:/customer/otp/" + customer.getId();
				}
			} else {
				session.setAttribute("failure", "Invalid Password");
				return "redirect:/customer/login";
			}
		}
	}

}
