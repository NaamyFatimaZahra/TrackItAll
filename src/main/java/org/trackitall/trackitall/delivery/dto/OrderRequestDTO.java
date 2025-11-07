package org.trackitall.trackitall.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Le client est obligatoire")
    private Long customerId;

    @NotNull(message = "Le produit est obligatoire")
    private Long productId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;
}