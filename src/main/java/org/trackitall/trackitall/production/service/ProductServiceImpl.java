package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.trackitall.trackitall.production.dto.BillOfMaterialRequestDTO;
import org.trackitall.trackitall.production.dto.BillOfMaterialResponseDTO;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.production.entity.BillOfMaterial;
import org.trackitall.trackitall.production.mapper.ProductMapper;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.production.repository.BillOfMaterialRepository;
import org.trackitall.trackitall.supply.repository.RawMaterialRepository;
import org.trackitall.trackitall.supply.entity.RawMaterial;
import org.trackitall.trackitall.production.service.IProductService;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO) {
        try {
            validateProductRequest(productDTO);
            validateBillOfMaterials(productDTO.getBillOfMaterials());

            Product product = productMapper.toEntity(productDTO);
            Product savedProduct = productRepository.save(product);

            List<BillOfMaterial> billOfMaterials = productDTO.getBillOfMaterials().stream()
                    .map(bomDTO -> {
                        RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId())
                                .orElseThrow(() -> new NotFoundException("Matériau non trouvé avec l'ID: " + bomDTO.getMaterialId()));

                        BillOfMaterial bom = new BillOfMaterial();
                        bom.setQuantity(bomDTO.getQuantity());
                        bom.setMaterial(material);
                        bom.setProduct(savedProduct);
                        return bom;
                    })
                    .collect(Collectors.toList());

            billOfMaterialRepository.saveAll(billOfMaterials);

            Product productWithBoms = productRepository.findByIdWithBillOfMaterials(savedProduct.getId())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé après création"));

            ProductResponseDTO response = buildProductResponse(productWithBoms);
            return response;

        } catch (BusinessException | ValidationException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la création du produit: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO) {
        try {
            Product existingProduct = productRepository.findByIdWithBillOfMaterials(id)
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

            validateProductRequestForUpdate(productDTO, id);

            if (productDTO.getBillOfMaterials() != null) {
                validateBillOfMaterials(productDTO.getBillOfMaterials());
            }

            productMapper.updateEntityFromDTO(productDTO, existingProduct);

            if (productDTO.getBillOfMaterials() != null) {
                updateBillOfMaterials(existingProduct, productDTO.getBillOfMaterials());
            }

            Product updatedProduct = productRepository.save(existingProduct);

            Product productWithBoms = productRepository.findByIdWithBillOfMaterials(updatedProduct.getId())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé après mise à jour"));

            return buildProductResponse(productWithBoms);

        } catch (NotFoundException | BusinessException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour du produit: " + e.getMessage());
        }
    }

    private void updateBillOfMaterials(Product product, List<BillOfMaterialRequestDTO> newBomDTOs) {

        List<BillOfMaterial> existingBoms = product.getBillOfMaterials();

        Map<Long, BillOfMaterial> existingBomsByMaterialId = existingBoms.stream()
                .collect(Collectors.toMap(
                        bom -> bom.getMaterial().getId(),
                        bom -> bom
                ));

        Set<Long> newMaterialIds = newBomDTOs.stream()
                .map(BillOfMaterialRequestDTO::getMaterialId)
                .collect(Collectors.toSet());

        List<BillOfMaterial> bomsToRemove = existingBoms.stream()
                .filter(bom -> !newMaterialIds.contains(bom.getMaterial().getId()))
                .collect(Collectors.toList());

        if (!bomsToRemove.isEmpty()) {
            billOfMaterialRepository.deleteAll(bomsToRemove);

            product.getBillOfMaterials().removeAll(bomsToRemove);
        }

        for (BillOfMaterialRequestDTO bomDTO : newBomDTOs) {
            RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId())
                    .orElseThrow(() -> new NotFoundException("Matériau non trouvé avec l'ID: " + bomDTO.getMaterialId()));

            BillOfMaterial existingBom = existingBomsByMaterialId.get(bomDTO.getMaterialId());

            if (existingBom != null) {

                existingBom.setQuantity(bomDTO.getQuantity());
            } else {

                BillOfMaterial newBom = new BillOfMaterial();
                newBom.setQuantity(bomDTO.getQuantity());
                newBom.setMaterial(material);
                newBom.setProduct(product);

                product.getBillOfMaterials().add(newBom);
            }
        }

        billOfMaterialRepository.saveAll(product.getBillOfMaterials());
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

            Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(id);
            if (activeOrdersCount > 0) {
                throw new BusinessException("Impossible de supprimer le produit car il a " + activeOrdersCount + " ordre(s) de production actif(s)");
            }

            productRepository.delete(product);

        } catch (NotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la suppression du produit: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        try {
            return productRepository.findAll(pageable)
                    .map(this::buildProductResponse);
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des produits: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                throw new ValidationException("Le terme de recherche ne peut pas être vide");
            }

            return productRepository.findByNameContaining(searchTerm, pageable)
                    .map(this::buildProductResponse);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la recherche des produits: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        try {
            Product product = productRepository.findByIdWithBillOfMaterials(id)
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

            return buildProductResponse(product);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération du produit: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByName(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Le nom du produit ne peut pas être vide");
            }

            Product product = productRepository.findByName(name)
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec le nom: " + name));

            Product productWithBoms = productRepository.findByIdWithBillOfMaterials(product.getId())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé"));

            return buildProductResponse(productWithBoms);

        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération du produit par nom: " + e.getMessage());
        }
    }

    private ProductResponseDTO buildProductResponse(Product product) {
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setProductionTime(product.getProductionTime());
        response.setCost(product.getCost());
        response.setStock(product.getStock());

        if (product.getBillOfMaterials() != null && !product.getBillOfMaterials().isEmpty()) {
            List<BillOfMaterialResponseDTO> bomResponses = product.getBillOfMaterials().stream()
                    .map(this::buildBomResponse)
                    .collect(Collectors.toList());
            response.setBillOfMaterials(bomResponses);
        }

        Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(product.getId());
        response.setActiveProductionOrdersCount(activeOrdersCount);

        return response;
    }

    private BillOfMaterialResponseDTO buildBomResponse(BillOfMaterial bom) {
        BillOfMaterialResponseDTO response = new BillOfMaterialResponseDTO();
        response.setId(bom.getId());
        response.setQuantity(bom.getQuantity());

        if (bom.getMaterial() != null) {
            RawMaterial material = bom.getMaterial();
            response.setMaterial(buildRawMaterialResponse(material));
        }

        return response;
    }

    private org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO buildRawMaterialResponse(RawMaterial material) {
        org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO response =
                new org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO();
        response.setId(material.getId());
        response.setName(material.getName());
        response.setStock(material.getStock());
        response.setStockMin(material.getStockMin());
        response.setUnit(material.getUnit());
        return response;
    }

    private void validateProductRequest(ProductRequestDTO productDTO) {
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Le nom du produit est obligatoire");
        }

        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            throw new BusinessException("Un produit avec ce nom existe déjà: " + productDTO.getName());
        }

        if (productDTO.getProductionTime() == null || productDTO.getProductionTime() <= 0) {
            throw new ValidationException("Le temps de production doit être supérieur à 0");
        }

        if (productDTO.getCost() == null || productDTO.getCost() <= 0) {
            throw new ValidationException("Le coût doit être supérieur à 0");
        }

        if (productDTO.getStock() == null || productDTO.getStock() < 0) {
            throw new ValidationException("Le stock ne peut pas être négatif");
        }
    }

    private void validateProductRequestForUpdate(ProductRequestDTO productDTO, Long productId) {
        if (productDTO.getName() != null && !productDTO.getName().trim().isEmpty()) {
            productRepository.findByName(productDTO.getName())
                    .ifPresent(product -> {
                        if (!product.getId().equals(productId)) {
                            throw new BusinessException("Un autre produit avec ce nom existe déjà: " + productDTO.getName());
                        }
                    });
        }

        if (productDTO.getProductionTime() != null && productDTO.getProductionTime() <= 0) {
            throw new ValidationException("Le temps de production doit être supérieur à 0");
        }

        if (productDTO.getCost() != null && productDTO.getCost() <= 0) {
            throw new ValidationException("Le coût doit être supérieur à 0");
        }

        if (productDTO.getStock() != null && productDTO.getStock() < 0) {
            throw new ValidationException("Le stock ne peut pas être négatif");
        }
    }

    private void validateBillOfMaterials(List<BillOfMaterialRequestDTO> billOfMaterials) {
        if (billOfMaterials == null || billOfMaterials.isEmpty()) {
            throw new ValidationException("Un produit doit avoir au moins un matériau dans sa nomenclature");
        }

        for (BillOfMaterialRequestDTO bom : billOfMaterials) {
            if (bom.getMaterialId() == null) {
                throw new ValidationException("L'ID du matériau est obligatoire pour chaque élément de la nomenclature");
            }

            if (bom.getQuantity() == null || bom.getQuantity() <= 0) {
                throw new ValidationException("La quantité doit être supérieure à 0 pour le matériau ID: " + bom.getMaterialId());
            }

            boolean materialExists = rawMaterialRepository.existsById(bom.getMaterialId());
            if (!materialExists) {
                throw new NotFoundException("Matériau non trouvé avec l'ID: " + bom.getMaterialId());
            }
        }

        List<Long> materialIds = billOfMaterials.stream()
                .map(BillOfMaterialRequestDTO::getMaterialId)
                .collect(Collectors.toList());

        Set<Long> uniqueMaterialIds = new HashSet<>(materialIds);
        if (uniqueMaterialIds.size() != materialIds.size()) {
            throw new ValidationException("Des matériaux en doublon ont été détectés dans la nomenclature");
        }
    }
}