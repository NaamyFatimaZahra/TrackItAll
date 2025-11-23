// SupplyOrderResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Data;
import org.trackitall.trackitall.enums.SupplyOrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyOrderResponseDTO {
    private Long id;
    private LocalDate date;
    private SupplyOrderStatus status;
    private SupplierResponseSimpleDTO supplier;
    private List<SupplyOrderItemResponseDTO> items;
}