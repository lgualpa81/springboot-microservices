package com.products.model.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
  private String sku;
  private String name;
  private String description;
  private Double price;
  private Boolean status;
}
