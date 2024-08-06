package com.products.services;

import com.products.model.dto.ProductRequest;
import com.products.model.dto.ProductResponse;
import com.products.model.entity.ProductEntity;
import com.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
  private final ProductRepository productRepository;

  public void addProduct(ProductRequest request) {
    ProductEntity product = ProductEntity.builder()
                                         .sku(request.getSku())
                                         .name(request.getName())
                                         .description(request.getDescription())
                                         .price(request.getPrice())
                                         .status(request.getStatus())
                                         .build();
    productRepository.save(product);
    log.info("Product added: {}", product);
  }

  public List<ProductResponse> getAllProducts() {
    List<ProductEntity> products = productRepository.findAll();
    return products.stream()
                   .map(this::mapToProductResponse)
                   .toList();
  }

  private ProductResponse mapToProductResponse(ProductEntity product) {
    return ProductResponse.builder()
                          .id(product.getId())
                          .sku(product.getSku())
                          .name(product.getName())
                          .description(product.getDescription())
                          .price(product.getPrice())
                          .status(product.getStatus())
                          .build();
  }
}
