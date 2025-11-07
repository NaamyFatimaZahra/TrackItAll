package org.trackitall.trackitall.supply.controller;

import org.trackitall.trackitall.supply.dto.SupplyOrderRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplyOrderResponseDTO;
import org.trackitall.trackitall.supply.service.ISupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/supply/orders")
@RequiredArgsConstructor
public class SupplyOrderController {

    private final ISupplyOrderService supplyOrderService;

    @PostMapping
    public ResponseEntity<SupplyOrderResponseDTO> createSupplyOrder(@Valid @RequestBody SupplyOrderRequestDTO supplyOrderDTO) {
        SupplyOrderResponseDTO created = supplyOrderService.createSupplyOrder(supplyOrderDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDTO> updateSupplyOrder(
            @PathVariable Long id,
            @Valid @RequestBody SupplyOrderRequestDTO supplyOrderDTO) {
        SupplyOrderResponseDTO updated = supplyOrderService.updateSupplyOrder(id, supplyOrderDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyOrder(@PathVariable Long id) {
        supplyOrderService.deleteSupplyOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SupplyOrderResponseDTO>> getAllSupplyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplyOrderResponseDTO> orders = supplyOrderService.getAllSupplyOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplyOrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        SupplyOrderResponseDTO updated = supplyOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDTO> getSupplyOrderById(@PathVariable Long id) {
        SupplyOrderResponseDTO order = supplyOrderService.getSupplyOrderById(id);
        return ResponseEntity.ok(order);
    }
}