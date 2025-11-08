package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.enums.ProductionOrderStatus;
import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IProductionOrderService {
    ProductionOrderResponseDTO createProductionOrder(ProductionOrderRequestDTO productionOrderDTO);
    ProductionOrderResponseDTO updateProductionOrder(Long id, ProductionOrderRequestDTO productionOrderDTO);
    void cancelProductionOrder(Long id);
    Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable);
    ProductionOrderResponseDTO getProductionOrderById(Long id);
    List<ProductionOrderResponseDTO> getProductionOrdersByStatus(String status);
    ProductionOrderResponseDTO updateProductionOrderStatus(Long id, String status);
    Boolean checkMaterialsAvailability(Long productionOrderId);
}