package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.entity.ProductionOrder;
import org.trackitall.trackitall.production.entity.BillOfMaterial;
import org.trackitall.trackitall.production.mapper.ProductionOrderMapper;
import org.trackitall.trackitall.production.repository.ProductionOrderRepository;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.production.service.IProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public ProductionOrderResponseDTO createProductionOrder(ProductionOrderRequestDTO productionOrderDTO) {

        if (!checkMaterialsAvailabilityForOrder(productionOrderDTO)) {
            throw new RuntimeException("Matériaux insuffisants pour créer l'ordre de production");
        }

        ProductionOrder productionOrder = productionOrderMapper.toEntity(productionOrderDTO);
        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(savedOrder);
        response.setMaterialsAvailable(true);
        response.setEstimatedDuration(calculateEstimatedDuration(savedOrder));

        return response;
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrder(Long id, ProductionOrderRequestDTO productionOrderDTO) {
        ProductionOrder existingOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de production non trouvé avec l'ID: " + id));


        if (!"PENDING".equals(existingOrder.getStatus().name())) {
            throw new RuntimeException("Impossible de modifier un ordre de production déjà commencé");
        }

        productionOrderMapper.updateEntityFromDTO(productionOrderDTO, existingOrder);
        ProductionOrder savedOrder = productionOrderRepository.save(existingOrder);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(savedOrder);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderDTO));
        response.setEstimatedDuration(calculateEstimatedDuration(savedOrder));

        return response;
    }

    @Override
    @Transactional
    public void cancelProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de production non trouvé avec l'ID: " + id));

        // Vérifier si l'ordre peut être annulé
        if (!"PENDING".equals(productionOrder.getStatus().name())) {
            throw new RuntimeException("Impossible d'annuler un ordre de production déjà commencé");
        }

        productionOrderRepository.delete(productionOrder);
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
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de production non trouvé avec l'ID: " + id));

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(productionOrder);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(productionOrder)));
        response.setEstimatedDuration(calculateEstimatedDuration(productionOrder));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponseDTO> getProductionOrdersByStatus(String status) {
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
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrderStatus(Long id, String status) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de production non trouvé avec l'ID: " + id));

        productionOrder.setStatus(org.trackitall.trackitall.enums.ProductionOrderStatus.valueOf(status));
        ProductionOrder updatedOrder = productionOrderRepository.save(productionOrder);

        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(updatedOrder);
        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(updatedOrder)));
        response.setEstimatedDuration(calculateEstimatedDuration(updatedOrder));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkMaterialsAvailability(Long productionOrderId) {
        ProductionOrder productionOrder = productionOrderRepository.findById(productionOrderId)
                .orElseThrow(() -> new RuntimeException("Ordre de production non trouvé avec l'ID: " + productionOrderId));
        return checkMaterialsAvailabilityForOrder(toRequest(productionOrder));
    }

    private Boolean checkMaterialsAvailabilityForOrder(ProductionOrderRequestDTO orderDTO) {
        return productRepository.findById(orderDTO.getProductId())
                .map(product -> product.getBillOfMaterials().stream()
                        .allMatch(bom -> bom.getMaterial().getStock() >= bom.getQuantity() * orderDTO.getQuantity()))
                .orElse(false);
    }

    private Integer calculateEstimatedDuration(ProductionOrder productionOrder) {
        return productionOrder.getProduct().getProductionTime() * productionOrder.getQuantity();
    }

    private ProductionOrderRequestDTO toRequest(ProductionOrder productionOrder) {
        ProductionOrderRequestDTO request = new ProductionOrderRequestDTO();
        request.setProductId(productionOrder.getProduct().getId());
        request.setQuantity(productionOrder.getQuantity());
        request.setStartDate(productionOrder.getStartDate());
        request.setEndDate(productionOrder.getEndDate());
        return request;
    }
}