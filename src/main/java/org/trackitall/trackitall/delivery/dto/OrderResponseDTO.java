package org.trackitall.trackitall.delivery.dto;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long id;
    private CustomerResponseDTO customer;
    private org.trackitall.trackitall.production.dto.ProductResponseDTO product;
    private Integer quantity;
    private String status;
    private String address;
    private DeliveryResponseDTO delivery;
}