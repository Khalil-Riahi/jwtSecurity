package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import jakarta.validation.Valid;




import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@GetMapping
	public ResponseEntity<?> getAllProducts(){
		return ResponseEntity.ok(productService.getAllProducts());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProduct(@PathVariable Long id){
		return ResponseEntity.ok(productService.getProduct(id));
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> addProduct(@RequestBody @Valid Product product){
		return ResponseEntity.ok(productService.addProduct(product));
	}
	
	@PutMapping
	@PreAuthorize("hasRole('Admin')")
	public ResponseEntity<?> updateProduct(@PathVariable Long id , @RequestBody @Valid Product product){
		return ResponseEntity.ok(productService.addProduct(product));
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('Admin')")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id){
		productService.deleteProduct(id);
		return ResponseEntity.ok("product deleted successfully");
	}
	
}
