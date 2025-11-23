package org.trackitall.trackitall.supply.dto;

import lombok.Data;
import org.trackitall.trackitall.enums.SupplyOrderStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyOrderRequestDTO {
    private Long id;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "Le statut est obligatoire")
    private SupplyOrderStatus status;

    @NotNull(message = "Le fournisseur est obligatoire")
    private Integer supplierId;

    private List<SupplyOrderItemRequestDTO> items;
}
