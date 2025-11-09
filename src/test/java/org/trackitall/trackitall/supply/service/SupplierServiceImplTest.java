package org.trackitall.trackitall.supply.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.Supplier;
import org.trackitall.trackitall.supply.mapper.SupplierMapper;
import org.trackitall.trackitall.supply.repository.SupplierRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private SupplierMapper supplierMapper;
    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Test
    void createSupplier() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO();
        supplierRequestDTO.setContact("74567890");
        supplierRequestDTO.setName("amin");
        supplierRequestDTO.setLeadTime(8);

        Supplier supplier = new Supplier();
        supplier.setName("amin");
        supplier.setLeadTime(8);
        supplier.setContact("74567890");

        SupplierResponseDTO supplierResponseDTO = new SupplierResponseDTO();
        supplierResponseDTO.setName("amin");
        supplierResponseDTO.setLeadTime(8);
        supplierResponseDTO.setContact("74567890");

        Mockito.when(supplierMapper.toEntity(supplierRequestDTO)).thenReturn(supplier);
        Mockito.when(supplierRepository.save(supplier)).thenReturn(supplier);
        Mockito.when(supplierMapper.toResponseDTO(supplier)).thenReturn(supplierResponseDTO);

        SupplierResponseDTO createSupplier = supplierService.createSupplier(supplierRequestDTO);

        Assertions.assertEquals(supplier.getName(), createSupplier.getName());
    }


    @Test
    void updateSupplier() {
    }

    @Test
    void deleteSupplier() {
    }

    @Test
    void getAllSuppliers() {
    }

    @Test
    void searchSuppliers() {
    }

    @Test
    void getSupplierById() {
    }
}