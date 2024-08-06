package com.inventory.services;

import com.inventory.model.dto.BaseResponse;
import com.inventory.model.dto.OrderItemRequest;
import com.inventory.model.entity.InventoryEntity;
import com.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
  private final InventoryRepository inventoryRepository;

  public boolean isInStock(String sku) {
    Optional<InventoryEntity> inventory = inventoryRepository.findBySku(sku);
    return inventory.filter(value -> value.getQuantity() > 0)
                    .isPresent();
  }

  public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
    List<String> errorList = new ArrayList<>();
    List<String> skus = orderItems.stream()
                                  .map(OrderItemRequest::getSku)
                                  .toList();
    List<InventoryEntity> inventoryList = inventoryRepository.findBySkuIn(skus);

    orderItems.forEach(orderItem -> {
      String itemSku = orderItem.getSku();
      Optional<InventoryEntity> inventory = inventoryList.stream()
                                                         .filter(value -> value.getSku()
                                                                               .equals(itemSku))
                                                         .findFirst();
      if (inventory.isEmpty()) {
        errorList.add("Product with sku " + itemSku + " does not exist");
      } else if (inventory.get()
                          .getQuantity() < orderItem.getQuantity()) {
        errorList.add("Product with sku " + itemSku + " has insufficient quantity");
      }
    });
    return !errorList.isEmpty() ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
  }
}
