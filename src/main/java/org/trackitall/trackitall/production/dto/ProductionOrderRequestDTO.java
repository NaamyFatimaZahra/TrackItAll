package org.trackitall.trackitall.production.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionOrderRequestDTO {
    @NotNull(message = "Le produit est obligatoire")
    private Long productId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;
}