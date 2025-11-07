package org.trackitall.trackitall.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DeliveryRequestDTO {
    @NotNull(message = "La commande est obligatoire")
    private Long orderId;

    @NotBlank(message = "Le véhicule est obligatoire")
    private String vehicule;

    @NotBlank(message = "Le chauffeur est obligatoire")
    private String driver;

    private LocalDate deliveryDate;

    @NotNull(message = "Le coût est obligatoire")
    private Double cost;
}