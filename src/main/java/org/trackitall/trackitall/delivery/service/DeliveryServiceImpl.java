package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.trackitall.trackitall.delivery.entity.Delivery;
import org.trackitall.trackitall.delivery.mapper.DeliveryMapper;
import org.trackitall.trackitall.delivery.repository.DeliveryRepository;
import org.trackitall.trackitall.delivery.service.IDeliveryService;
import org.trackitall.trackitall.enums.DeliveryStatus;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryDTO) {
        try {
            validateDeliveryRequest(deliveryDTO);

            if (deliveryRepository.findByOrderId(deliveryDTO.getOrderId()).isPresent()) {
                throw new ValidationException("Une livraison existe déjà pour la commande avec l'ID: " + deliveryDTO.getOrderId());
            }

            Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
            Delivery savedDelivery = deliveryRepository.save(delivery);

            return enrichDeliveryResponse(savedDelivery);

        } catch (ValidationException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la création de la livraison: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDelivery(Long id, DeliveryRequestDTO deliveryDTO) {
        try {

            validateDeliveryRequest(deliveryDTO);

            Delivery existingDelivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Livraison non trouvée avec l'ID: " + id));

            if (!existingDelivery.getOrder().getId().equals(deliveryDTO.getOrderId())) {
                if (deliveryRepository.findByOrderId(deliveryDTO.getOrderId()).isPresent()) {
                    throw new ValidationException("Une livraison existe déjà pour la commande avec l'ID: " + deliveryDTO.getOrderId());
                }
            }

            deliveryMapper.updateEntityFromDTO(deliveryDTO, existingDelivery);
            Delivery updatedDelivery = deliveryRepository.save(existingDelivery);

            return enrichDeliveryResponse(updatedDelivery);

        } catch (NotFoundException | ValidationException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour de la livraison: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDelivery(Long id) {
        try {


            Delivery delivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Livraison non trouvée avec l'ID: " + id));

            if (DeliveryStatus.LIVREE.equals(delivery.getStatus())) {
                throw new ValidationException("Impossible de supprimer une livraison déjà livrée");
            }

            deliveryRepository.delete(delivery);


        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la suppression de la livraison: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        try {
            return deliveryRepository.findAll(pageable)
                    .map(this::enrichDeliveryResponse);
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des livraisons: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryById(Long id) {
        try {
            Delivery delivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Livraison non trouvée avec l'ID: " + id));

            return enrichDeliveryResponse(delivery);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération de la livraison: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getDeliveriesByStatus(String status) {
        try {
          DeliveryStatus statusEnum = validateAndConvertDeliveryStatus(status);

            return deliveryRepository.findByStatus(statusEnum).stream()
                    .map(this::enrichDeliveryResponse)
                    .collect(Collectors.toList());
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des livraisons par statut: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(Long id, String status) {
        try {
            Delivery delivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Livraison non trouvée avec l'ID: " + id));

            DeliveryStatus statusEnum = validateAndConvertDeliveryStatus(status);
            validateDeliveryStatusTransition(delivery.getStatus(), statusEnum);

            delivery.setStatus(statusEnum);
            if (DeliveryStatus.LIVREE.equals(statusEnum)) {
                delivery.setDeliveryDate(LocalDate.now());
            }

            Delivery updatedDelivery = deliveryRepository.save(delivery);

            return enrichDeliveryResponse(updatedDelivery);

        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour du statut de la livraison: " + e.getMessage());
        }
    }

    private DeliveryResponseDTO enrichDeliveryResponse(Delivery delivery) {
        try {
            DeliveryResponseDTO response = deliveryMapper.toResponseDTO(delivery);
            if (delivery.getOrder() != null && delivery.getOrder().getProduct() != null) {
                Double totalCost = delivery.getCost() + (delivery.getOrder().getProduct().getCost() * delivery.getOrder().getQuantity());
                response.setTotalCost(totalCost);
            } else {
                response.setTotalCost(delivery.getCost());
            }

            return response;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la préparation de la réponse de livraison: " + e.getMessage());
        }
    }

    private void validateDeliveryRequest(DeliveryRequestDTO deliveryDTO) {
        if (deliveryDTO == null) {
            throw new ValidationException("Les données de la livraison ne peuvent pas être nulles");
        }
        if (deliveryDTO.getOrderId() == null) {
            throw new ValidationException("L'ID de la commande est obligatoire");
        }
        if (deliveryDTO.getCost() == null || deliveryDTO.getCost() < 0) {
            throw new ValidationException("Le coût de livraison doit être supérieur ou égal à zéro");
        }
        if (deliveryDTO.getDeliveryDate() != null && deliveryDTO.getDeliveryDate().isBefore(LocalDate.now())) {
            throw new ValidationException("La date de livraison ne peut pas être dans le passé");
        }
    }

    private DeliveryStatus validateAndConvertDeliveryStatus(String status) {
        try {
            return DeliveryStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut de livraison invalide: " + status +
                    ". Statuts valides: " + java.util.Arrays.toString(DeliveryStatus.values()));
        }
    }

    private void validateDeliveryStatusTransition(DeliveryStatus currentStatus,
                                                 DeliveryStatus newStatus) {

        if (currentStatus == DeliveryStatus.LIVREE &&
                newStatus != DeliveryStatus.LIVREE) {
            throw new ValidationException("Impossible de modifier le statut d'une livraison déjà livrée");
        }

        if (currentStatus == DeliveryStatus.LIVREE &&
                newStatus == DeliveryStatus.EN_COURS) {
            throw new ValidationException("Impossible de repasser une livraison livrée en statut 'en cours'");
        }
    }
}