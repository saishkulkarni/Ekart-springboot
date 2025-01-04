package org.jsp.ekart.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Size(min = 5, max = 30, message = "* Enter Between 5~30 Charecters")
	private String name;
	@DecimalMin(value = "6000000000", message = "* Enter Proper Mobile Number")
	@DecimalMax(value = "9999999999", message = "* Enter Proper Mobile Number")
	private long mobile;
	@Email(message = "* Enter Proper Email")
	@NotEmpty(message = "* It is Required Field")
	private String email;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Enter atleast 8 charecters consisting of one uppercase, one lowercase, one number, one special charecter")
	private String password;
	@Transient
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Enter atleast 8 charecters consisting of one uppercase, one lowercase, one number, one special charecter")
	private String confirmPassword;
	private int otp;
	private boolean verified;

	@OneToOne(cascade = CascadeType.ALL)
	Cart cart=new Cart();
}
