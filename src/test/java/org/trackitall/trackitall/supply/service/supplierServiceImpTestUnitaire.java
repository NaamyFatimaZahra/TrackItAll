package org.trackitall.trackitall.supply.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.RawMaterial;
import org.trackitall.trackitall.supply.entity.Supplier;
import org.trackitall.trackitall.supply.mapper.SupplierMapper;
import org.trackitall.trackitall.supply.repository.RawMaterialRepository;
import org.trackitall.trackitall.supply.repository.SupplierRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class supplierServiceImpTestUnitaire {

    @Mock private SupplierRepository supplierRepository;
    @Mock private RawMaterialRepository rawMaterialRepository;
    @Mock private SupplierMapper supplierMapper;

    @InjectMocks private SupplierServiceImpl service;


    @Test
    void createSupplier_simple() {
        SupplierRequestDTO dto = new SupplierRequestDTO();
        dto.setName("amin");
        dto.setRawMaterialId(List.of(1));

        Supplier entity = new Supplier();
        entity.setRawMaterials(new ArrayList<>());

        RawMaterial rm = new RawMaterial();
        rm.setId(1L);

        SupplierResponseDTO response = new SupplierResponseDTO();
        response.setName("amin");

        when(supplierRepository.findByName("amin")).thenReturn(Optional.empty());
        when(supplierMapper.toEntity(dto)).thenReturn(entity);
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rm));
        when(supplierRepository.save(entity)).thenReturn(entity);
        when(supplierMapper.toResponseDTO(entity)).thenReturn(response);

        SupplierResponseDTO result = service.createSupplier(dto);

        assertEquals("amin", result.getName());
    }


    @Test
    void updateSupplier_simple() {
        SupplierRequestDTO dto = new SupplierRequestDTO();
        dto.setName("updated");
        dto.setRawMaterialId(List.of());

        Supplier entity = new Supplier();
        SupplierResponseDTO response = new SupplierResponseDTO();
        response.setName("updated");

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(supplierRepository.save(entity)).thenReturn(entity);
        when(supplierMapper.toResponseDTO(entity)).thenReturn(response);

        SupplierResponseDTO result = service.updateSupplier(1L, dto);

        assertEquals("updated", result.getName());
    }


    @Test
    void deleteSupplier_simple() {
        Supplier entity = new Supplier();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(supplierRepository.hasActiveOrders(1L)).thenReturn(false);

        assertDoesNotThrow(() -> service.deleteSupplier(1L));
        verify(supplierRepository).delete(entity);
    }

    @Test
    void getAllSuppliers_simple() {
        Supplier supplier = new Supplier();
        SupplierResponseDTO dto = new SupplierResponseDTO();

        Page<Supplier> page = new PageImpl<>(List.of(supplier));

        when(supplierRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(dto);

        Page<SupplierResponseDTO> result = service.getAllSuppliers(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }


    @Test
    void searchSuppliers_simple() {
        Supplier supplier = new Supplier();
        SupplierResponseDTO dto = new SupplierResponseDTO();

        when(supplierRepository.findByNameContaining(eq("a"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(supplier)));
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(dto);

        Page<SupplierResponseDTO> result = service.searchSuppliers("a", Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }


    @Test
    void getSupplierById_simple() {
        Supplier supplier = new Supplier();
        SupplierResponseDTO dto = new SupplierResponseDTO();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(dto);

        SupplierResponseDTO result = service.getSupplierById(1L);

        assertNotNull(result);
    }
}
