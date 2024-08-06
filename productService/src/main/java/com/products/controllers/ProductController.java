package com.products.controllers;

import com.products.model.dto.ProductRequest;
import com.products.model.dto.ProductResponse;
import com.products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void addProduct(@RequestBody ProductRequest payload) {
    this.productService.addProduct(payload);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ProductResponse> getAllProducts() {
    return this.productService.getAllProducts();
  }
}
