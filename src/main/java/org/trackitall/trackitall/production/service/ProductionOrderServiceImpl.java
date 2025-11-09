package org.trackitall.trackitall.production.service;

import org.trackitall.trackitall.enums.ProductionOrderStatus;
import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.entity.ProductionOrder;
import org.trackitall.trackitall.production.entity.BillOfMaterial;
import org.trackitall.trackitall.production.mapper.ProductionOrderMapper;
import org.trackitall.trackitall.production.repository.ProductionOrderRepository;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.production.service.IProductionOrderService;
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
public class ProductionOrderServiceImpl implements IProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    @Transactional
    public ProductionOrderResponseDTO createProductionOrder(ProductionOrderRequestDTO productionOrderDTO) {
        try {
            if (!checkMaterialsAvailabilityForOrder(productionOrderDTO)) {
                throw new BusinessException("Matériaux insuffisants pour créer l'ordre de production");
            }

            ProductionOrder productionOrder = productionOrderMapper.toEntity(productionOrderDTO);
            ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);

            ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(savedOrder);
            response.setMaterialsAvailable(true);
            response.setEstimatedDuration(calculateEstimatedDuration(savedOrder));

            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la création de l'ordre de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrder(Long id, ProductionOrderRequestDTO productionOrderDTO) {
        try {
            ProductionOrder existingOrder = productionOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

            if (!ProductionOrderStatus.EN_ATTENTE.equals(existingOrder.getStatus())) {
                throw new ValidationException("Impossible de modifier un ordre de production déjà commencé");
            }

            productionOrderMapper.updateEntityFromDTO(productionOrderDTO, existingOrder);
            ProductionOrder savedOrder = productionOrderRepository.save(existingOrder);

            ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(savedOrder);
            response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(productionOrderDTO));
            response.setEstimatedDuration(calculateEstimatedDuration(savedOrder));

            return response;
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour de l'ordre de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancelProductionOrder(Long id) {
        try {
            ProductionOrder productionOrder = productionOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

            if (!ProductionOrderStatus.EN_ATTENTE.equals(productionOrder.getStatus())) {
                throw new ValidationException("Impossible d'annuler un ordre de production déjà commencé");
            }

            productionOrderRepository.delete(productionOrder);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de l'annulation de l'ordre de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable) {
        try {
            return productionOrderRepository.findAll(pageable)
                    .map(order -> {
                        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(order)));
                        response.setEstimatedDuration(calculateEstimatedDuration(order));
                        return response;
                    });
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des ordres de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionOrderResponseDTO getProductionOrderById(Long id) {
        try {
            ProductionOrder productionOrder = productionOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

            ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(productionOrder);
            response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(productionOrder)));
            response.setEstimatedDuration(calculateEstimatedDuration(productionOrder));

            return response;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération de l'ordre de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderResponseDTO> getProductionOrdersByStatus(ProductionOrderStatus status) {
        try {
            return productionOrderRepository.findByStatus(status).stream()
                    .map(order -> {
                        ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(order);
                        response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(order)));
                        response.setEstimatedDuration(calculateEstimatedDuration(order));
                        return response;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des ordres de production par statut: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductionOrderResponseDTO updateProductionOrderStatus(Long id, ProductionOrderStatus status) {
        try {
            ProductionOrder productionOrder = productionOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + id));

            productionOrder.setStatus(status);
            ProductionOrder updatedOrder = productionOrderRepository.save(productionOrder);

            ProductionOrderResponseDTO response = productionOrderMapper.toResponseDTO(updatedOrder);
            response.setMaterialsAvailable(checkMaterialsAvailabilityForOrder(toRequest(updatedOrder)));
            response.setEstimatedDuration(calculateEstimatedDuration(updatedOrder));

            return response;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour du statut de l'ordre de production: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkMaterialsAvailability(Long productionOrderId) {
        try {
            ProductionOrder productionOrder = productionOrderRepository.findById(productionOrderId)
                    .orElseThrow(() -> new NotFoundException("Ordre de production non trouvé avec l'ID: " + productionOrderId));
            return checkMaterialsAvailabilityForOrder(toRequest(productionOrder));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la vérification de la disponibilité des matériaux: " + e.getMessage());
        }
    }

    private Boolean checkMaterialsAvailabilityForOrder(ProductionOrderRequestDTO orderDTO) {
        try {
            return productRepository.findById(orderDTO.getProductId())
                    .map(product -> {
                        if (product.getBillOfMaterials() == null || product.getBillOfMaterials().isEmpty()) {
                            throw new BusinessException("Le produit n'a pas de nomenclature définie");
                        }
                        return product.getBillOfMaterials().stream()
                                .allMatch(bom -> {
                                    if (bom.getMaterial() == null) {
                                        throw new BusinessException("Matériau non défini dans la nomenclature");
                                    }
                                    return bom.getMaterial().getStock() >= bom.getQuantity() * orderDTO.getQuantity();
                                });
                    })
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + orderDTO.getProductId()));
        } catch (NotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la vérification de la disponibilité des matériaux: " + e.getMessage());
        }
    }

    private Integer calculateEstimatedDuration(ProductionOrder productionOrder) {
        try {
            if (productionOrder.getProduct() == null) {
                throw new BusinessException("Produit non défini pour l'ordre de production");
            }
            if (productionOrder.getProduct().getProductionTime() == null) {
                throw new BusinessException("Temps de production non défini pour le produit");
            }
            return productionOrder.getProduct().getProductionTime() * productionOrder.getQuantity();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors du calcul de la durée estimée: " + e.getMessage());
        }
    }

    private ProductionOrderRequestDTO toRequest(ProductionOrder productionOrder) {
        try {
            ProductionOrderRequestDTO request = new ProductionOrderRequestDTO();
            if (productionOrder.getProduct() == null) {
                throw new BusinessException("Produit non défini pour l'ordre de production");
            }
            request.setProductId(productionOrder.getProduct().getId());
            request.setQuantity(productionOrder.getQuantity());
            request.setStartDate(productionOrder.getStartDate());
            request.setEndDate(productionOrder.getEndDate());
            return request;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la conversion de l'entité en DTO: " + e.getMessage());
        }
    }
}