package org.trackitall.trackitall.production.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionOrderResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Integer quantity;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean materialsAvailable;
    private Integer estimatedDuration;
}