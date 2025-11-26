package org.trackitall.trackitall.delivery.dto;

import jakarta.validation.constraints.Min;
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
    @Min(value = 1,message = "La quantité ne doit pas être inférieure à 1")
    private Integer quantity;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;
}