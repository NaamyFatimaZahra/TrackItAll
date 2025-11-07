package org.trackitall.trackitall.delivery.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DeliveryResponseDTO {
    private Long id;
    private OrderResponseDTO order;
    private String vehicule;
    private String driver;
    private String status;
    private LocalDate deliveryDate;
    private Double cost;
    private Double totalCost;
}