package org.trackitall.trackitall.production.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trackitall.trackitall.enums.ProductionOrderStatus;
import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.entity.ProductionOrder;
import org.trackitall.trackitall.production.mapper.ProductionOrderMapper;
import org.trackitall.trackitall.production.repository.ProductionOrderRepository;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl implements IProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    @Transactional
    public ProductionOrderResponseDTO createProductionOrder(ProductionOrderRequestDTO dto) {
        if (!checkMaterialsAvailabilityForOrder(dto)) {
            throw new BusinessException("Matériaux insuffisants pour créer l'ordre de production");
        }

        ProductionOrder order = productionOrderMapper.toEntity(dto);
        ProductionOrder saved = productionOrderRepository.save(order);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(saved);
        response.setMaterialsAvailable(true);
        response.setEstimatedDuration(calculateEstimatedDuration(saved));

        return response;
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrder(Long id, ProductionOrderRequestDTO dto) {
        ProductionOrder existing = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

        if (!ProductionOrderStatus.EN_ATTENTE.equals(existing.getStatus())) {
            throw new ValidationException("Impossible de modifier un ordre de production déjà commencé");
        }

        ProductionOrder saved = productionOrderRepository.save(existing);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(saved);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(dto));
        response.setEstimatedDuration(calculateEstimatedDuration(saved));

        return response;
    }

    @Override
    @Transactional
    public void cancelProductionOrder(Long id) {
        ProductionOrder existing = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

        if (!ProductionOrderStatus.EN_ATTENTE.equals(existing.getStatus())) {
            throw new ValidationException("Impossible d'annuler un ordre de production déjà commencé");
        }

        productionOrderRepository.delete(existing);
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrderStatus(Long id, ProductionOrderStatus status) {
        ProductionOrder existing = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

        existing.setStatus(status);
        ProductionOrder saved = productionOrderRepository.save(existing);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(saved);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(saved)));
        response.setEstimatedDuration(calculateEstimatedDuration(saved));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable)
                .map(order -> {
                    ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                    response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(order)));
                    response.setEstimatedDuration(calculateEstimatedDuration(order));
                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionOrderResponseDTO getProductionOrderById(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(order)));
        response.setEstimatedDuration(calculateEstimatedDuration(order));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponseDTO> getProductionOrdersByStatus(ProductionOrderStatus status) {
        return productionOrderRepository.findByStatus(status).stream()
                .map(order -> {
                    ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                    response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(order)));
                    response.setEstimatedDuration(calculateEstimatedDuration(order));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkMaterialsAvailability(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));
        return checkMaterialsAvailabilityForOrder(toRequest(order));
    }

    private Boolean checkMaterialsAvailabilityForOrder(ProductionOrderRequestDTO dto) {
        return productRepository.findById(dto.getProductId())
                .map(product -> {
                    if (product.getBillOfMaterials() == null || product.getBillOfMaterials().isEmpty()) {
                        throw new BusinessException("Le produit n'a pas de nomenclature définie");
                    }
                    return product.getBillOfMaterials().stream()
                            .allMatch(bom -> bom.getMaterial().getStock() >= bom.getQuantity() * dto.getQuantity());
                })
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + dto.getProductId()));
    }

    private Integer calculateEstimatedDuration(ProductionOrder order) {
        if (order.getProduct() == null || order.getProduct().getProductionTime() == null) {
            throw new BusinessException("Temps de production non défini pour le produit");
        }
        return order.getProduct().getProductionTime() * order.getQuantity();
    }

    private ProductionOrderRequestDTO toRequest(ProductionOrder order) {
        ProductionOrderRequestDTO dto = new ProductionOrderRequestDTO();
        dto.setProductId(order.getProduct().getId());
        dto.setQuantity(order.getQuantity());
        dto.setStartDate(order.getStartDate());
        dto.setEndDate(order.getEndDate());
        return dto;
    }
}
