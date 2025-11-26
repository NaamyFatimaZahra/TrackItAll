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

        productRepository.findByName(productDTO.getName()).ifPresent(p -> {
            throw new BusinessException("Un produit avec ce nom existe déjà : " + productDTO.getName());
        });
        Product product = productMapper.toEntity(productDTO);

        if (productDTO.getBillOfMaterials() != null) {
            List<BillOfMaterial> billOfMaterials = productDTO.getBillOfMaterials().stream()
                    .map(bomDTO -> {
                        RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId())
                                .orElseThrow(() -> new NotFoundException(
                                        "Matériau non trouvé avec l'ID: " + bomDTO.getMaterialId()));

                        BillOfMaterial bom = new BillOfMaterial();
                        bom.setQuantity(bomDTO.getQuantity());
                        bom.setMaterial(material);
                        bom.setProduct(product);
                        return bom;
                    })
                    .collect(Collectors.toList());

            product.setBillOfMaterials(billOfMaterials);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO) {
        Product existingProduct = productRepository.findByIdWithBillOfMaterials(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

        productRepository.findByName(productDTO.getName())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new BusinessException("Un produit avec ce nom existe déjà : " + productDTO.getName());
                });

        productMapper.updateEntityFromDTO(productDTO, existingProduct);
        if (productDTO.getBillOfMaterials() != null) {
            List<BillOfMaterial> newBoms = productDTO.getBillOfMaterials().stream()
                    .map(bomDTO -> {
                        RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId())
                                .orElseThrow(() -> new NotFoundException(
                                        "Matériau non trouvé avec l'ID: " + bomDTO.getMaterialId()));

                        BillOfMaterial bom = new BillOfMaterial();
                        bom.setQuantity(bomDTO.getQuantity());
                        bom.setMaterial(material);
                        bom.setProduct(existingProduct);
                        return bom;
                    })
                    .collect(Collectors.toList());

            existingProduct.setBillOfMaterials(newBoms);
        }

        Product savedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

        Integer activeOrdersCount = productRepository.countActiveProductionOrdersByProductId(id);
        if (activeOrdersCount > 0) {
            throw new BusinessException("Impossible de supprimer le produit car il a " + activeOrdersCount + " ordre(s) de production actif(s)");
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
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ValidationException("Le terme de recherche ne peut pas être vide");
        }

        return productRepository.findByNameContaining(searchTerm, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findByIdWithBillOfMaterials(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + id));

        return productMapper.toResponseDTO(product);
    }
}
