package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.dto.RawMaterialRequestDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialRequestDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IRawMaterialService {

    RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO rawMaterialDTO);

    RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialRequestDTO rawMaterialDTO);

    void deleteRawMaterial(Long id);

    Page<RawMaterialResponseDTO> getAllRawMaterials(Pageable pageable);

    Page<RawMaterialResponseDTO> searchRawMaterials(String name, Pageable pageable);

    List<RawMaterialResponseDTO> getLowStockMaterials();

    RawMaterialResponseDTO getRawMaterialById(Long id);
}