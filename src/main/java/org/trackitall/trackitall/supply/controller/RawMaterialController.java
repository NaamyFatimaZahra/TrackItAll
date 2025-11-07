package org.trackitall.trackitall.supply.controller;

import org.trackitall.trackitall.supply.dto.RawMaterialRequestDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;
import org.trackitall.trackitall.supply.service.IRawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/supply/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final IRawMaterialService rawMaterialService;

    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> createRawMaterial(@Valid @RequestBody RawMaterialRequestDTO rawMaterialDTO) {
        RawMaterialResponseDTO created = rawMaterialService.createRawMaterial(rawMaterialDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> updateRawMaterial(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialRequestDTO rawMaterialDTO) {
        RawMaterialResponseDTO updated = rawMaterialService.updateRawMaterial(id, rawMaterialDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<RawMaterialResponseDTO>> getAllRawMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RawMaterialResponseDTO> rawMaterials = rawMaterialService.getAllRawMaterials(pageable);
        return ResponseEntity.ok(rawMaterials);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RawMaterialResponseDTO>> searchRawMaterials(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RawMaterialResponseDTO> rawMaterials = rawMaterialService.searchRawMaterials(name, pageable);
        return ResponseEntity.ok(rawMaterials);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<RawMaterialResponseDTO>> getLowStockMaterials() {
        List<RawMaterialResponseDTO> lowStockMaterials = rawMaterialService.getLowStockMaterials();
        return ResponseEntity.ok(lowStockMaterials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> getRawMaterialById(@PathVariable Long id) {
        RawMaterialResponseDTO rawMaterial = rawMaterialService.getRawMaterialById(id);
        return ResponseEntity.ok(rawMaterial);
    }
}