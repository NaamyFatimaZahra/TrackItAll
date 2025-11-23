package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.entity.Supplier;
import org.trackitall.trackitall.supply.repository.SupplierRepository;
import org.trackitall.trackitall.supply.service.IRawMaterialService;
import org.trackitall.trackitall.supply.dto.RawMaterialRequestDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;
import org.trackitall.trackitall.supply.entity.RawMaterial;
import org.trackitall.trackitall.supply.mapper.RawMaterialMapper;
import org.trackitall.trackitall.supply.repository.RawMaterialRepository;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements IRawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    @Transactional
    public RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO rawMaterialDTO) {
        if (rawMaterialRepository.findByName(rawMaterialDTO.getName()).isPresent()) {
            throw new BusinessException("Une matière première avec ce nom existe déjà");
        }
        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);
        if(rawMaterial.getSuppliers()==null){
            rawMaterial.setSuppliers(new ArrayList<>());
        }
        rawMaterialDTO.getSupplierIds().stream().filter(sp->sp!=0 && sp!=null).forEach(
                sp->{
                    Supplier supplier=supplierRepository.findById(sp).orElseThrow(
                            ()->new BusinessException( "Supplier avec id " + sp + " n'existe pas.")
                    );
                    //for DB
                    supplier.getRawMaterials().add(rawMaterial);
                    //for Response
                    rawMaterial.getSuppliers().add(supplier);
                }
        );
        RawMaterial saved = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialRequestDTO rawMaterialDTO) {
        RawMaterial existing = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));
        existing.setName(rawMaterialDTO.getName());
        existing.setStock(rawMaterialDTO.getStock());
        existing.setStockMin(rawMaterialDTO.getStockMin());
        existing.setUnit(rawMaterialDTO.getUnit());
        rawMaterialRepository.save(existing);
        return rawMaterialMapper.toResponseDTO(existing);
    }

    @Override
    @Transactional
    public void deleteRawMaterial(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));

        if (rawMaterialRepository.existsByIdAndSupplyOrdersIsNotEmpty(id)) {
            throw new BusinessException("Impossible de supprimer une matière première utilisée dans des commandes");
        }

        rawMaterial.getSuppliers().stream().forEach(
                sp->{
                    sp.getRawMaterials().remove(rawMaterial);
                }
        );
        rawMaterialRepository.delete(rawMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> getAllRawMaterials(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> searchRawMaterials(String name, Pageable pageable) {
        return rawMaterialRepository.findByNameContaining(name, pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponseDTO> getLowStockMaterials() {
        return rawMaterialRepository.findLowStockMaterials()
                .stream()
                .map(rawMaterialMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialResponseDTO getRawMaterialById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));
        return rawMaterialMapper.toResponseDTO(rawMaterial);
    }
}