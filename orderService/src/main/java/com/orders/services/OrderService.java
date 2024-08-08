package com.orders.services;

import com.orders.events.OrderEvent;
import com.orders.model.dto.*;
import com.orders.model.entity.OrderEntity;
import com.orders.model.entity.OrderItemsEntity;
import com.orders.model.enums.OrderStatus;
import com.orders.repository.OrderRepository;
import com.orders.utils.JsonUtils;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final WebClient.Builder webClientBuilder;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObservationRegistry observationRegistry;

  public List<OrderResponse> getAllOrders() {
    List<OrderEntity> orderList = this.orderRepository.findAll();
    return orderList.stream()
                    .map(this::mapToOrderResponse)
                    .toList();
  }

  public OrderResponse placeOrder(OrderRequest orderRequest) {
    Observation inventoryObservation = Observation.createNotStarted("inventoryService", observationRegistry);
    //check for inventory
    return inventoryObservation.observe(() -> {
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
        //TODO: Send message to order topic
        this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                new OrderEvent(saveOrder.getOrderNumber(), saveOrder.getOrderItems()
                                                                    .size(), OrderStatus.PLACED)
        ));
        return mapToOrderResponse(saveOrder);
      } else {
        throw new IllegalArgumentException("Some of the products are not in stock");
      }
    });
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
