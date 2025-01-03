package org.jsp.ekart.repository;

import java.util.List;

import org.jsp.ekart.dto.Product;
import org.jsp.ekart.dto.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByVendor(Vendor vendor);

	List<Product> findByApprovedTrue();

	List<Product> findByNameLike(String toSearch);

	List<Product> findByDescriptionLike(String toSearch);

	List<Product> findByCategoryLike(String toSearch);

}
