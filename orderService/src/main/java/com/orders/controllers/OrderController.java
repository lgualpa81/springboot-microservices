package com.orders.controllers;

import com.orders.model.dto.OrderRequest;
import com.orders.model.dto.OrderResponse;
import com.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public String placeOrder(@RequestBody OrderRequest orderRequest) {
    this.orderService.placeOrder(orderRequest);
    return "Order placed successfully";
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<OrderResponse> getOrders() {
    return this.orderService.getAllOrders();
  }
}
