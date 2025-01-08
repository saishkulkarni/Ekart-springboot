package org.jsp.ekart.repository;

import java.util.List;

import org.jsp.ekart.dto.Customer;
import org.jsp.ekart.dto.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByCustomer(Customer customer);
}
