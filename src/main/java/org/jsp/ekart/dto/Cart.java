package org.jsp.ekart.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Cart {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	List<Item> items = new ArrayList<Item>();
}
