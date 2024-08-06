package com.inventory.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
  private Long id;
  private String sku;
  private Double price;
  private Long quantity;
}
