package com.inventory.model.dto;

public record BaseResponse(String[] errorMessages) {
  public boolean hasErrors() {
    return errorMessages != null && errorMessages.length > 0;
  }
}
