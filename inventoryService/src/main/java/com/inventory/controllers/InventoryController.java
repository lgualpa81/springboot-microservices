package com.inventory.controllers;

import com.inventory.model.dto.BaseResponse;
import com.inventory.model.dto.OrderItemRequest;
import com.inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
  private final InventoryService inventoryService;

  @GetMapping("/{sku}")
  @ResponseStatus(HttpStatus.OK)
  public boolean isInStock(@PathVariable("sku") String sku) {
    return inventoryService.isInStock(sku);
  }

  @PostMapping("/in-stock")
  @ResponseStatus(HttpStatus.OK)
  public BaseResponse areInStock(@RequestBody List<OrderItemRequest> orderItems) {
    return inventoryService.areInStock(orderItems);
  }
}
