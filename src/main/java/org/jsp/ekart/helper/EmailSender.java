package org.jsp.ekart.helper;

import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.dto.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSender {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine templateEngine;

	public void send(Vendor customer) {
		String email = customer.getEmail();
		int otp = customer.getOtp();
		String name = customer.getName();

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("saishkulkarni7@gmail.com", "Ekart Site");
			helper.setTo(email);
			helper.setSubject("Otp for Email Verification");
			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			String text = templateEngine.process("otp-email.html", context);

			helper.setText(text, true);

			mailSender.send(message);
		} catch (Exception e) {
			System.err.println("There is Some Issue");
		}
	}

	public void send(Customer customer) {
		String email = customer.getEmail();
		int otp = customer.getOtp();
		String name = customer.getName();

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("saishkulkarni7@gmail.com", "Ekart Site");
			helper.setTo(email);
			helper.setSubject("Otp for Email Verification");
			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			String text = templateEngine.process("otp-email.html", context);

			helper.setText(text, true);

			mailSender.send(message);
		} catch (Exception e) {
			System.err.println("There is Some Issue");
		}
	}
}
