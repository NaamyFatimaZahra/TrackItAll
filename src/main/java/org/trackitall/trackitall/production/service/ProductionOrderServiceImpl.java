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

        order.setProduct(productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + dto.getProductId())));

        order.setStatus(ProductionOrderStatus.EN_ATTENTE);

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
            throw new BusinessException("Impossible de modifier un ordre de production déjà commencé");
        }

        productionOrderMapper.updateEntityFromDTO(dto, existing);

        existing.setProduct(productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + dto.getProductId())));

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
            throw new BusinessException("Impossible d'annuler un ordre de production déjà commencé");
        }

        productionOrderRepository.delete(existing);
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrderStatus(Long id, ProductionOrderStatus status) {
        ProductionOrder existing = productionOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

        existing.setStatus(status);

        if (ProductionOrderStatus.EN_PRODUCTION.equals(status)) {

            if (existing.getProduct() == null || existing.getProduct().getBillOfMaterials() == null) {
                throw new BusinessException("Le produit n'a pas de nomenclature (BOM) définie");
            }

            existing.getProduct().getBillOfMaterials().forEach(bom -> {
                int quantityNeeded = bom.getQuantity() * existing.getQuantity();
                int newStock = bom.getMaterial().getStock() - quantityNeeded;

                if (newStock < 0) {
                    throw new BusinessException(
                            "Stock insuffisant pour le matériau : " + bom.getMaterial().getName()
                    );
                }

                bom.getMaterial().setStock(newStock);
            });
        }

        ProductionOrder saved = productionOrderRepository.save(existing);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(saved);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderMapper.toRequestDTO(saved)));
        response.setEstimatedDuration(calculateEstimatedDuration(saved));

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable)
                .map(order -> {
                    ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                    response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderMapper.toRequestDTO(order)));
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
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderMapper.toRequestDTO(order)));
        response.setEstimatedDuration(calculateEstimatedDuration(order));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponseDTO> getProductionOrdersByStatus(ProductionOrderStatus status) {
        return productionOrderRepository.findByStatus(status).stream()
                .map(order -> {
                    ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                    response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderMapper.toRequestDTO(order)));
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
        return checkMaterialsAvailabilityForOrder(productionOrderMapper.toRequestDTO(order));
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


}
