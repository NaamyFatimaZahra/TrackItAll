package org.trackitall.trackitall.supply.service;

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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements IRawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    @Transactional
    public RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO rawMaterialDTO) {
        if (rawMaterialRepository.findByName(rawMaterialDTO.getName()).isPresent()) {
            throw new BusinessException("Une matière première avec ce nom existe déjà");
        }

        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);
        RawMaterial saved = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toResponseDTOWithStockInfo(saved);
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

        RawMaterial updated = rawMaterialRepository.save(existing);
        return rawMaterialMapper.toResponseDTOWithStockInfo(updated);
    }

    @Override
    @Transactional
    public void deleteRawMaterial(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));

        if (rawMaterialRepository.existsByIdAndSupplyOrdersIsNotEmpty(id)) {
            throw new BusinessException("Impossible de supprimer une matière première utilisée dans des commandes");
        }

        rawMaterialRepository.delete(rawMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> getAllRawMaterials(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(rawMaterialMapper::toResponseDTOWithStockInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> searchRawMaterials(String name, Pageable pageable) {
        return rawMaterialRepository.findByNameContaining(name, pageable)
                .map(rawMaterialMapper::toResponseDTOWithStockInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponseDTO> getLowStockMaterials() {
        return rawMaterialRepository.findLowStockMaterials()
                .stream()
                .map(rawMaterialMapper::toResponseDTOWithStockInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialResponseDTO getRawMaterialById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));
        return rawMaterialMapper.toResponseDTOWithStockInfo(rawMaterial);
    }
}