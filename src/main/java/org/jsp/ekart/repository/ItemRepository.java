package org.jsp.ekart.repository;

import org.jsp.ekart.dto.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {

}
