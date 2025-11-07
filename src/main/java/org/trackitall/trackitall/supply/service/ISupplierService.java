package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISupplierService {

    SupplierResponseDTO createSupplier(SupplierRequestDTO supplierDTO);

    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO supplierDTO);

    void deleteSupplier(Long id);

    Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable);

    Page<SupplierResponseDTO> searchSuppliers(String name, Pageable pageable);

    SupplierResponseDTO getSupplierById(Long id);
}