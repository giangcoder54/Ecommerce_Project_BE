package com.ecommerce.library.dto;

import java.util.Date;
import java.util.List;

public class CheckoutDto {

  private Long customerId;
  private Date deliveryDate;
  private String address;
  private List<ProductQuantity> productQuantities;
  private String notes;

  // Default constructor
  public CheckoutDto() {
  }

  // Full constructor
  public CheckoutDto(Long customerId, Date deliveryDate, List<ProductQuantity> productQuantities, String notes, String address) {
    this.customerId = customerId;
    this.deliveryDate = deliveryDate;
    this.address = address;
    this.productQuantities = productQuantities;
    this.notes = notes;
  }

  // Getters and setters
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String addressString) {
    this.address = addressString;
  }

  public List<ProductQuantity> getProductQuantities() {
    return productQuantities;
  }

  public void setProductQuantities(List<ProductQuantity> productQuantities) {
    this.productQuantities = productQuantities;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  // Inner class to handle product and quantity
  public static class ProductQuantity {
    private Long productId;
    private int quantity;

    public ProductQuantity() {
    }

    public ProductQuantity(Long productId, int quantity) {
      this.productId = productId;
      this.quantity = quantity;
    }

    public Long getProductId() {
      return productId;
    }

    public void setProductId(Long productId) {
      this.productId = productId;
    }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }
  }
}
