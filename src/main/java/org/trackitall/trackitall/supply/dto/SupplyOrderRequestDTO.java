package org.trackitall.trackitall.supply.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.trackitall.trackitall.enums.SupplyOrderStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyOrderRequestDTO {
    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long supplierId;
    @Size(min = 1, message = "Il doit y avoir au moins un item")
    @Valid
    private List<SupplyOrderItemRequestDTO> items;
}
