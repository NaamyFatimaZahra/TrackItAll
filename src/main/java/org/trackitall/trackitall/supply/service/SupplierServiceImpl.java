package org.trackitall.trackitall.supply.service;

import org.apache.catalina.LifecycleState;
import org.trackitall.trackitall.supply.entity.RawMaterial;
import org.trackitall.trackitall.supply.repository.RawMaterialRepository;
import org.trackitall.trackitall.supply.service.ISupplierService;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.Supplier;
import org.trackitall.trackitall.supply.mapper.SupplierMapper;
import org.trackitall.trackitall.supply.repository.SupplierRepository;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO supplierDTO) {
        if (supplierRepository.findByName(supplierDTO.getName()).isPresent()) {
            throw new BusinessException("Un fournisseur avec ce nom existe déjà");
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        if (supplier.getRawMaterials() == null) {
            supplier.setRawMaterials(new ArrayList<>());
        }
        List<Integer> rawMaterialId=supplierDTO.getRawMaterialId();
        rawMaterialId.forEach(rm->{
           if (rm!=0){
               RawMaterial rawMaterial=rawMaterialRepository.findById(Long.valueOf(rm)).orElseThrow(()->{
                   throw new BusinessException("raw material avec id: "+rm+" n'exist pas.");
               });
               supplier.getRawMaterials().add(rawMaterial);
           }
        });
        Supplier saved = supplierRepository.save(supplier);

        return supplierMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO supplierDTO) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));

        existing.setName(supplierDTO.getName());
        existing.setContact(supplierDTO.getContact());
        existing.setRating(supplierDTO.getRating());
        existing.setLeadTime(supplierDTO.getLeadTime());
        supplierDTO.getRawMaterialId().stream().filter(rm->rm!=null && rm!=0).forEach(rm->{


                   RawMaterial rawMaterial=rawMaterialRepository.findById(Long.valueOf(rm)).orElseThrow(()->{
                       throw new BusinessException("raw material avec id: "+rm+" n'exist pas.");
                   });

                if(!existing.getRawMaterials().contains(rawMaterial)){
                    existing.getRawMaterials().add(rawMaterial);
                }

        });

        Supplier updated = supplierRepository.save(existing);
        return supplierMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));

        if (supplierRepository.hasActiveOrders(id)) {
            throw new BusinessException("Impossible de supprimer un fournisseur avec des commandes actives");
        }

        supplierRepository.delete(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> searchSuppliers(String name, Pageable pageable) {
        return supplierRepository.findByNameContaining(name, pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));
        return supplierMapper.toResponseDTO(supplier);
    }
}