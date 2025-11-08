package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO);
    void deleteProduct(Long id);
    Page<ProductResponseDTO> getAllProducts(Pageable pageable);
    Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable);
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO getProductByName(String name);
}