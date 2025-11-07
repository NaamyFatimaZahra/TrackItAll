package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.mapper.RawMaterialMapper;
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
import java.util.List;
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
    @Override
    @Transactional
    public SupplyOrderResponseDTO createSupplyOrder(SupplyOrderRequestDTO supplyOrderDTO) {
        Supplier supplier = supplierRepository.findById(supplyOrderDTO.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));

        SupplyOrder supplyOrder = supplyOrderMapper.toEntity(supplyOrderDTO);
        supplyOrder.setSupplier(supplier);

        SupplyOrder savedOrder = supplyOrderRepository.save(supplyOrder);

        if (supplyOrderDTO.getItems() != null) {
            List<SupplyOrderRawMaterial> orderItems = supplyOrderDTO.getItems().stream()
                    .map(item -> {
                        RawMaterial rawMaterial = rawMaterialRepository.findById(item.getRawMaterialId())
                                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));

                        SupplyOrderRawMaterial orderItem = new SupplyOrderRawMaterial();
                        orderItem.setSupplyOrder(savedOrder);
                        orderItem.setRawMaterial(rawMaterial);
                        orderItem.setQuantity(item.getQuantity());
                        return orderItem;
                    })
                    .collect(Collectors.toList());

            supplyOrderRawMaterialRepository.saveAll(orderItems);
        }

        return getSupplyOrderById(savedOrder.getId());
    }

    @Override
    @Transactional
    public SupplyOrderResponseDTO updateSupplyOrder(Long id, SupplyOrderRequestDTO supplyOrderDTO) {
        SupplyOrder existing = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));

        if (existing.getStatus() == SupplyOrderStatus.RECUE) {
            throw new BusinessException("Impossible de modifier une commande déjà livrée");
        }

        Supplier supplier = supplierRepository.findById(supplyOrderDTO.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Fournisseur non trouvé"));

        existing.setDate(supplyOrderDTO.getDate());
        existing.setStatus(supplyOrderDTO.getStatus());
        existing.setSupplier(supplier);

        if (supplyOrderDTO.getItems() != null) {
            supplyOrderRawMaterialRepository.deleteBySupplyOrderId(id);

            List<SupplyOrderRawMaterial> orderItems = supplyOrderDTO.getItems().stream()
                    .map(item -> {
                        RawMaterial rawMaterial = rawMaterialRepository.findById(item.getRawMaterialId())
                                .orElseThrow(() -> new NotFoundException("Matière première non trouvée"));

                        SupplyOrderRawMaterial orderItem = new SupplyOrderRawMaterial();
                        orderItem.setSupplyOrder(existing);
                        orderItem.setRawMaterial(rawMaterial);
                        orderItem.setQuantity(item.getQuantity());
                        return orderItem;
                    })
                    .collect(Collectors.toList());

            supplyOrderRawMaterialRepository.saveAll(orderItems);
        }

        SupplyOrder updated = supplyOrderRepository.save(existing);
        return getSupplyOrderById(updated.getId());
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
                .map(this::mapToResponseDTOWithItems);
    }

    @Override
    @Transactional
    public SupplyOrderResponseDTO updateOrderStatus(Long id, String status) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));

        try {
            SupplyOrderStatus newStatus = SupplyOrderStatus.valueOf(status.toUpperCase());
            supplyOrder.setStatus(newStatus);

            SupplyOrder updated = supplyOrderRepository.save(supplyOrder);
            return getSupplyOrderById(updated.getId());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SupplyOrderResponseDTO getSupplyOrderById(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));
        return mapToResponseDTOWithItems(supplyOrder);
    }

    private SupplyOrderResponseDTO mapToResponseDTOWithItems(SupplyOrder supplyOrder) {
        SupplyOrderResponseDTO responseDTO = supplyOrderMapper.toResponseDTO(supplyOrder);

        List<SupplyOrderRawMaterial> orderItems = supplyOrderRawMaterialRepository.findBySupplyOrderId(supplyOrder.getId());
        List<SupplyOrderItemResponseDTO> itemDTOs = orderItems.stream()
                .map(item -> {
                    SupplyOrderItemResponseDTO itemDTO = new SupplyOrderItemResponseDTO();
                    itemDTO.setRawMaterial(rawMaterialMapper.toResponseDTO(item.getRawMaterial()));
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setItems(itemDTOs);
        return responseDTO;
    }
}