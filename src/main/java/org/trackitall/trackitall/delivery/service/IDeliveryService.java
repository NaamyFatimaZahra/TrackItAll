package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.trackitall.trackitall.enums.DeliveryStatus;

import java.util.List;

public interface IDeliveryService {
    DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryDTO);
    DeliveryResponseDTO updateDelivery(Long id, DeliveryRequestDTO deliveryDTO);
    void deleteDelivery(Long id);
    Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable);
    DeliveryResponseDTO getDeliveryById(Long id);
    List<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status);
    DeliveryResponseDTO updateDeliveryStatus(Long id, DeliveryStatus status);
}