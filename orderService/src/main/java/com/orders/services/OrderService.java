package com.orders.services;

import com.orders.model.dto.*;
import com.orders.model.entity.OrderEntity;
import com.orders.model.entity.OrderItemsEntity;
import com.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final WebClient.Builder webClientBuilder;

  public List<OrderResponse> getAllOrders() {
    List<OrderEntity> orderList = this.orderRepository.findAll();
    return orderList.stream()
                    .map(this::mapToOrderResponse)
                    .toList();
  }

  public OrderResponse placeOrder(OrderRequest orderRequest) {
    //check for inventory
    BaseResponse result = this.webClientBuilder.build()
                                               .post()
                                               .uri("lb://inventoryService/api/inventory/in-stock")
                                               .bodyValue(orderRequest.getOrderItems())
                                               .retrieve()
                                               .bodyToMono(BaseResponse.class)
                                               .block();
    if (result != null && !result.hasErrors()) {
      OrderEntity order = new OrderEntity();
      order.setOrderNumber(UUID.randomUUID()
                               .toString());
      order.setOrderItems(orderRequest.getOrderItems()
                                      .stream()
                                      .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                                      .toList());
      OrderEntity saveOrder = this.orderRepository.save(order);
      return mapToOrderResponse(saveOrder);
    } else {
      throw new IllegalArgumentException("Some of the products are not in stock");
    }
  }

  private OrderResponse mapToOrderResponse(OrderEntity order) {
    return new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderItems()
                                                                         .stream()
                                                                         .map(this::mapToOrderItemRequest)
                                                                         .toList());
  }

  private OrderItemsResponse mapToOrderItemRequest(OrderItemsEntity orderItems) {
    return new OrderItemsResponse(orderItems.getId(), orderItems.getSku(), orderItems.getPrice(), orderItems.getQuantity());
  }

  private OrderItemsEntity mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, OrderEntity order) {
    return OrderItemsEntity.builder()
                           .id(orderItemRequest.getId())
                           .sku(orderItemRequest.getSku())
                           .price(orderItemRequest.getPrice())
                           .quantity(orderItemRequest.getQuantity())
                           .order(order)
                           .build();
  }

}
