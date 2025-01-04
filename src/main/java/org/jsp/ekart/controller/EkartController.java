package org.jsp.ekart.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.jsp.ekart.dto.Cart;
import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.dto.Item;
import org.jsp.ekart.dto.Product;
import org.jsp.ekart.dto.Vendor;
import org.jsp.ekart.helper.CloudinaryHelper;
import org.jsp.ekart.repository.CustomerRepository;
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

/**
 * Main controller class for handling E-commerce operations
 */
@Controller
public class EkartController {

	// Admin credentials from properties file
	@Value("${admin.email}")
	String adminEmail;
	@Value("${admin.password}")
	String adminPassword;

	// Service layer dependencies
	@Autowired
	VendorService vendorService;

	@Autowired
	CustomerService customerService;

	@Autowired
	CloudinaryHelper cloudinaryHelper;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CustomerRepository customerRepository;

	/**
	 * Loads the main home page
	 */
	@GetMapping("/")
	public String loadHomePage() {
		return "home.html";
	}

	/**
	 * Loads OTP verification page for vendor
	 */
	@GetMapping("/vendor/otp/{id}")
	public String loadOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "vendor-otp.html";
	}

	/**
	 * Loads vendor registration page
	 */
	@GetMapping("/vendor/register")
	public String loadVendorRegistration(ModelMap map, Vendor vendor) {
		return vendorService.loadRegistration(map, vendor);
	}

	/**
	 * Handles vendor registration form submission
	 */
	@PostMapping("/vendor/register")
	public String vendorRegistration(@Valid Vendor vendor, BindingResult result, HttpSession session) {
		return vendorService.registration(vendor, result, session);
	}

	/**
	 * Verifies vendor OTP
	 */
	@PostMapping("/vendor/otp")
	public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return vendorService.verifyOtp(id, otp, session);
	}

	/**
	 * Loads vendor login page
	 */
	@GetMapping("/vendor/login")
	public String loadLogin() {
		return "vendor-login.html";
	}

	/**
	 * Handles vendor login
	 */
	@PostMapping("/vendor/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return vendorService.login(email, password, session);
	}

	/**
	 * Loads vendor home page after successful login
	 */
	@GetMapping("/vendor/home")
	public String loadHome(HttpSession session) {
		if (session.getAttribute("vendor") != null)
			return "vendor-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	/**
	 * Handles logout functionality
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("vendor");
		session.setAttribute("success", "Logged out Success");
		return "redirect:/";
	}

	/**
	 * Loads add product page for vendor
	 */
	@GetMapping("/add-product")
	public String loadAddProduct(HttpSession session) {
		if (session.getAttribute("vendor") != null)
			return "add-product.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	/**
	 * Handles adding new product by vendor
	 */
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

	/**
	 * Shows list of products for vendor management
	 */
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

	/**
	 * Handles product deletion
	 */
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

	/**
	 * Loads admin login page
	 */
	@GetMapping("/admin/login")
	public String loadAdminLogin() {
		return "admin-login.html";
	}

	/**
	 * Handles admin login authentication
	 */
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

	/**
	 * Loads admin home page
	 */
	@GetMapping("/admin/home")
	public String loadAdminHome(HttpSession session) {
		if (session.getAttribute("admin") != null)
			return "admin-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/admin/login";
		}
	}

	/**
	 * Shows products pending approval to admin
	 */
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

	/**
	 * Handles changing product approval status
	 */
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

	/**
	 * Loads customer OTP verification page
	 */
	@GetMapping("/customer/otp/{id}")
	public String loadCustomerOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "customer-otp.html";
	}

	/**
	 * Loads customer registration page
	 */
	@GetMapping("/customer/register")
	public String loadCustomerRegistration(ModelMap map, Customer customer) {
		return customerService.loadRegistration(map, customer);
	}

	/**
	 * Handles customer registration
	 */
	@PostMapping("/customer/register")
	public String customerRegistration(@Valid Customer customer, BindingResult result, HttpSession session) {
		return customerService.registration(customer, result, session);
	}

	/**
	 * Verifies customer OTP
	 */
	@PostMapping("/customer/otp")
	public String verifyCustomerOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return customerService.verifyOtp(id, otp, session);
	}

	/**
	 * Loads customer login page
	 */
	@GetMapping("/customer/login")
	public String loadCustomerLogin() {
		return "customer-login.html";
	}

	/**
	 * Handles customer login
	 */
	@PostMapping("/customer/login")
	public String customerLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return customerService.login(email, password, session);
	}

	/**
	 * Loads customer home page
	 */
	@GetMapping("/customer/home")
	public String loadCustomerHome(HttpSession session) {
		if (session.getAttribute("customer") != null)
			return "customer-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

	/**
	 * Loads product editing page for vendor
	 */
	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, ModelMap map, HttpSession session) {
		if (session.getAttribute("vendor") != null) {
			Product product = productRepository.findById(id).get();
			map.put("product", product);
			return "edit-product.html";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	/**
	 * Handles product update by vendor
	 */
	@PostMapping("/update-product")
	public String updateProduct(Product product, HttpSession session) throws IOException {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			product.setImageLink(cloudinaryHelper.saveToCloudinary(product.getImage()));
			product.setVendor(vendor);
			productRepository.save(product);
			session.setAttribute("success", "Product Updated Success");
			return "redirect:/manage-products";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

	/**
	 * Shows approved products to customers
	 */
	@GetMapping("/view-products")
	public String viewProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			List<Product> products = productRepository.findByApprovedTrue();
			if (products.isEmpty()) {
				session.setAttribute("failure", "No Products Present");
				return "redirect:/customer/home";
			} else {
				map.put("products", products);
				return "customer-view-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

	/**
	 * Loads product search page
	 */
	@GetMapping("/search-products")
	public String searchProducts(HttpSession session) {
		if (session.getAttribute("customer") != null) {
			return "search.html";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

	/**
	 * Handles product search functionality
	 */
	@PostMapping("/search-products")
	public String search(@RequestParam String query, HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			String toSearch = "%" + query + "%";
			List<Product> list1 = productRepository.findByNameLike(toSearch);
			List<Product> list2 = productRepository.findByDescriptionLike(toSearch);
			List<Product> list3 = productRepository.findByCategoryLike(toSearch);
			HashSet<Product> products = new HashSet<Product>();
			products.addAll(list1);
			products.addAll(list2);
			products.addAll(list3);
			map.put("products", products);
			map.put("query", query);
			return "search.html";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

	@GetMapping("/view-cart")
	public String viewCart(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			Cart cart = customer.getCart();
			if (cart == null) {
				session.setAttribute("failure", "Nothing is Present inside Cart");
				return "redirect:/customer/home";
			} else {
				List<Item> items = cart.getItems();
				if (items.isEmpty()) {
					session.setAttribute("failure", "Nothing is Present inside Cart");
					return "redirect:/customer/home";
				} else {
					map.put("items", items);
					return "view-cart.html";
				}
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

	@GetMapping("/add-cart/{id}")
	public String addToCart(@PathVariable int id, HttpSession session) {
		if (session.getAttribute("customer") != null) {
			Product product = productRepository.findById(id).get();
			if (product.getStock() > 0) {
				Customer customer = (Customer) session.getAttribute("customer");

				Cart cart = customer.getCart();
				List<Item> items = cart.getItems();

				if (items.stream().map(x -> x.getName()).collect(Collectors.toList()).contains(product.getName())) {
					session.setAttribute("failure", "Product Already Exists in Cart");
					return "redirect:/customer/home";
				} else {
					Item item = new Item();
					item.setName(product.getName());
					item.setCategory(product.getCategory());
					item.setDescription(product.getDescription());
					item.setImageLink(product.getImageLink());
					item.setPrice(product.getPrice());
					item.setQuantity(1);
					items.add(item);
					
					customerRepository.save(customer);
					session.setAttribute("success", "Product Added to Cart Success");
					session.setAttribute("customer", customerRepository.findById(customer.getId()).get());
					return "redirect:/customer/home";
				}

			} else {
				session.setAttribute("failure", "Sorry! Product Out of Stock");
				return "redirect:/customer/home";
			}

		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/customer/login";
		}
	}

}