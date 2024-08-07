package com.products.controllers;

import com.products.model.dto.ProductRequest;
import com.products.model.dto.ProductResponse;
import com.products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void addProduct(@RequestBody ProductRequest payload) {
    this.productService.addProduct(payload);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ROLE_USER')")
  public List<ProductResponse> getAllProducts() {
    return this.productService.getAllProducts();
  }
}
