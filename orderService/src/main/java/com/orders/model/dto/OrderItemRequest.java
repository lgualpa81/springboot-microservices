package com.orders.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
  private Long id;
  private String sku;
  private Double price;
  private Long quantity;
}
