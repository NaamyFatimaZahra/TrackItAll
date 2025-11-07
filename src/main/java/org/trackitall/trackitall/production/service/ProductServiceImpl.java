package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.production.entity.BillOfMaterial;
import org.trackitall.trackitall.production.mapper.ProductMapper;
import org.trackitall.trackitall.production.mapper.BillOfMaterialMapper;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.production.repository.BillOfMaterialRepository;
import org.trackitall.trackitall.production.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final ProductMapper productMapper;
    private final BillOfMaterialMapper billOfMaterialMapper;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO) {
        // Vérifier si la référence existe déjà
        if (productDTO.getReference() != null &&
                productRepository.findByReference(productDTO.getReference()).isPresent()) {
            throw new RuntimeException("Un produit avec cette référence existe déjà");
        }

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        // Sauvegarder les BOMs
        if (productDTO.getBillOfMaterials() != null && !productDTO.getBillOfMaterials().isEmpty()) {
            List<BillOfMaterial> billOfMaterials = productDTO.getBillOfMaterials().stream()
                    .map(bomDTO -> {
                        BillOfMaterial bom = billOfMaterialMapper.toEntity(bomDTO);
                        bom.setProduct(savedProduct);
                        return bom;
                    })
                    .collect(Collectors.toList());
            billOfMaterialRepository.saveAll(billOfMaterials);
        }

        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        // Vérifier la référence si elle est modifiée
        if (productDTO.getReference() != null &&
                !productDTO.getReference().equals(existingProduct.getReference()) &&
                productRepository.findByReference(productDTO.getReference()).isPresent()) {
            throw new RuntimeException("Un produit avec cette référence existe déjà");
        }

        productMapper.updateEntityFromDTO(productDTO, existingProduct);

        // Mettre à jour les BOMs
        if (productDTO.getBillOfMaterials() != null) {
            // Supprimer les anciens BOMs
            billOfMaterialRepository.deleteByProductId(id);

            // Ajouter les nouveaux BOMs
            List<BillOfMaterial> billOfMaterials = productDTO.getBillOfMaterials().stream()
                    .map(bomDTO -> {
                        BillOfMaterial bom = billOfMaterialMapper.toEntity(bomDTO);
                        bom.setProduct(existingProduct);
                        return bom;
                    })
                    .collect(Collectors.toList());
            billOfMaterialRepository.saveAll(billOfMaterials);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(id);
        if (activeOrdersCount > 0) {
            throw new RuntimeException("Impossible de supprimer le produit car il a des ordres de production actifs");
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.findByNameOrReferenceContaining(searchTerm, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
        ProductResponseDTO response = productMapper.toResponseDTO(product);

        // Calculer les ordres de production actifs
        Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(id);
        response.setActiveProductionOrdersCount(activeOrdersCount);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByReference(String reference) {
        Product product = productRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec la référence: " + reference));
        ProductResponseDTO response = productMapper.toResponseDTO(product);

        // Calculer les ordres de production actifs
        Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(product.getId());
        response.setActiveProductionOrdersCount(activeOrdersCount);

        return response;
    }
}