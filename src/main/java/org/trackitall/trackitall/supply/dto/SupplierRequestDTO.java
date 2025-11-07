package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SupplierRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;

    @NotNull(message = "Le délai de livraison est obligatoire")
    private Integer leadTime;

}
