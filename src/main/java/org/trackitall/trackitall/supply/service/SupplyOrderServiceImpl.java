package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.mapper.RawMaterialMapper;
import org.trackitall.trackitall.supply.mapper.SupplyOrderItemsMapper;
import org.trackitall.trackitall.supply.service.ISupplyOrderService;
import org.trackitall.trackitall.supply.dto.*;
import org.trackitall.trackitall.supply.entity.*;
import org.trackitall.trackitall.supply.mapper.SupplyOrderMapper;
import org.trackitall.trackitall.supply.repository.*;
import org.trackitall.trackitall.enums.SupplyOrderStatus;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyOrderServiceImpl implements ISupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderRawMaterialRepository supplyOrderRawMaterialRepository;
    private final SupplyOrderMapper supplyOrderMapper;
    private final RawMaterialMapper rawMaterialMapper;
    private final SupplyOrderItemsMapper supplyOrderItemsMapper;
    @Override
    @Transactional
    public SupplyOrderResponseDTO createSupplyOrder(SupplyOrderRequestDTO dto) {

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));

        SupplyOrder order = supplyOrderMapper.toEntity(dto);
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setSupplier(supplier);
        Set<Long> seenRawMaterialIds = new HashSet<>();

        order.setItems(
                dto.getItems().stream()
                        .filter(itemDTO -> seenRawMaterialIds.add(itemDTO.getRawMaterialId()))
                        .map(itemDTO -> {
                            RawMaterial raw = rawMaterialRepository.findById(itemDTO.getRawMaterialId())
                                    .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));

                            SupplyOrderRawMaterial item = supplyOrderItemsMapper.toEntity(itemDTO);
                            item.setRawMaterial(raw);
                            item.setSupplyOrder(order);
                            return item;
                        })
                        .toList()
        );
        SupplyOrder saved = supplyOrderRepository.save(order);

        return supplyOrderMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSupplyOrder(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));

        if (supplyOrder.getStatus() != SupplyOrderStatus.EN_ATTENTE) {
            throw new BusinessException("Impossible de supprimer une commande déjà en cours ou livrée");
        }

        supplyOrderRawMaterialRepository.deleteBySupplyOrderId(id);
        supplyOrderRepository.delete(supplyOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyOrderResponseDTO> getAllSupplyOrders(Pageable pageable) {
        return supplyOrderRepository.findAll(pageable)
                .map(supplyOrderMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public SupplyOrderResponseDTO updateOrderStatus(Long id, String status) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));

            SupplyOrderStatus newStatus = SupplyOrderStatus.valueOf(status.toUpperCase());
            supplyOrder.setStatus(newStatus);

            if(newStatus==SupplyOrderStatus.RECUE){
                supplyOrder.getItems().stream().forEach(
                        item->{
                            item.getRawMaterial().setStock(item.getRawMaterial().getStock()+ item.getQuantity());
                        }
                );
            }
            return supplyOrderMapper.toResponseDTO(supplyOrder);

    }

    @Override
    public SupplyOrderResponseDTO getSupplyOrderById(Long id) {
        SupplyOrder supplyOrder=supplyOrderRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Commande non trouvée"));
        return supplyOrderMapper.toResponseDTO(supplyOrder);
    }


}