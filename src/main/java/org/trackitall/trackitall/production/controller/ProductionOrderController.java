package org.trackitall.trackitall.production.controller;

import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.service.IProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final IProductionOrderService productionOrderService;

    @PostMapping
    public ResponseEntity<ProductionOrderResponseDTO> createProductionOrder(@Valid @RequestBody ProductionOrderRequestDTO productionOrderDTO) {
        ProductionOrderResponseDTO createdOrder = productionOrderService.createProductionOrder(productionOrderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> updateProductionOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionOrderRequestDTO productionOrderDTO) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.updateProductionOrder(id, productionOrderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelProductionOrder(@PathVariable Long id) {
        productionOrderService.cancelProductionOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getAllProductionOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductionOrderResponseDTO> orders = productionOrderService.getAllProductionOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> getProductionOrderById(@PathVariable Long id) {
        ProductionOrderResponseDTO order = productionOrderService.getProductionOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductionOrderResponseDTO>> getProductionOrdersByStatus(@PathVariable String status) {
        List<ProductionOrderResponseDTO> orders = productionOrderService.getProductionOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductionOrderResponseDTO> updateProductionOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.updateProductionOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{id}/materials-availability")
    public ResponseEntity<Boolean> checkMaterialsAvailability(@PathVariable Long id) {
        Boolean available = productionOrderService.checkMaterialsAvailability(id);
        return ResponseEntity.ok(available);
    }
}