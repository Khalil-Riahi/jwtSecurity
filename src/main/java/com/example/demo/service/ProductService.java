package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.example.demo.Repository.ProductRepository;
import com.example.demo.model.Product;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
	@Autowired
	private ProductRepository productRepo;
	
	public List<Product> getAllProducts(){
		return productRepo.findAll();
	}
	
	public Optional<Product> getProduct(Long id) {
		return productRepo.findById(id);
	}
	 
	public Product addProduct(Product product) {
		return productRepo.save(product);
	}
	
	public void deleteProduct(Long id) {
		 productRepo.deleteById(id);
	}
	
	
}
