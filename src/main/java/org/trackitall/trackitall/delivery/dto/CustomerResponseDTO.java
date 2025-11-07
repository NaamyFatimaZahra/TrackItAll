package org.trackitall.trackitall.delivery.dto;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String city;
    private Integer activeOrdersCount;
}