package org.trackitall.trackitall.supply.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import org.trackitall.trackitall.enums.SupplyOrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyOrderRequestSimpleDTO {
    private Long id;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
    @NotNull(message = "Le statut est obligatoire")
    private SupplyOrderStatus status;
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long supplierId;

    private List<SupplyOrderItemRequestDTO> items;
}
