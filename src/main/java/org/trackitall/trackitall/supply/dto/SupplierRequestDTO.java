package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SupplierRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;
    @NotNull(message = "rating est obligatoire")
    @Max(value = 5,message = "le rating ne peut pas etre superieur a 5")
    @Min(value = 1,message = "le rating ne peut pas etre inferieur a 1")
    private double rating;

    @NotNull(message = "Le délai de livraison est obligatoire")
    @Min(value = 1,message = "le lead Time ne peut pas etre inferieur a 1")
    private int leadTime;

    private List<Integer> rawMaterialId;

}
