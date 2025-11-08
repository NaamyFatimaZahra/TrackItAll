package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.trackitall.trackitall.delivery.entity.Delivery;
import org.trackitall.trackitall.delivery.mapper.DeliveryMapper;
import org.trackitall.trackitall.delivery.repository.DeliveryRepository;
import org.trackitall.trackitall.delivery.service.IDeliveryService;
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
        // Vérifier si une livraison existe déjà pour cette commande
        if (deliveryRepository.findByOrderId(deliveryDTO.getOrderId()).isPresent()) {
            throw new RuntimeException("Une livraison existe déjà pour cette commande");
        }

        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return enrichDeliveryResponse(savedDelivery);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDelivery(Long id, DeliveryRequestDTO deliveryDTO) {
        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec l'ID: " + id));

        deliveryMapper.updateEntityFromDTO(deliveryDTO, existingDelivery);
        Delivery updatedDelivery = deliveryRepository.save(existingDelivery);
        return enrichDeliveryResponse(updatedDelivery);
    }

    @Override
    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec l'ID: " + id));

        deliveryRepository.delete(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(this::enrichDeliveryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec l'ID: " + id));
        return enrichDeliveryResponse(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getDeliveriesByStatus(String status) {
        return deliveryRepository.findByStatus(status).stream()
                .map(this::enrichDeliveryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(Long id, String status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec l'ID: " + id));

        delivery.setStatus(org.trackitall.trackitall.enums.DeliveryStatus.valueOf(status));

        // Si la livraison est marquée comme livrée, mettre à jour la date de livraison
        if (org.trackitall.trackitall.enums.DeliveryStatus.LIVREE.name().equals(status)) {
            delivery.setDeliveryDate(LocalDate.now());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return enrichDeliveryResponse(updatedDelivery);
    }

    private DeliveryResponseDTO enrichDeliveryResponse(Delivery delivery) {
        DeliveryResponseDTO response = deliveryMapper.toResponseDTO(delivery);

        // Calculer le coût total (coût de livraison + coût des produits)
        Double totalCost = delivery.getCost() + (delivery.getOrder().getProduct().getCost() * delivery.getOrder().getQuantity());
        response.setTotalCost(totalCost);

        return response;
    }
}