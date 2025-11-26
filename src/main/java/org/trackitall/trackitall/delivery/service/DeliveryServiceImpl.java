package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.trackitall.trackitall.delivery.entity.Delivery;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.mapper.DeliveryMapper;
import org.trackitall.trackitall.delivery.repository.DeliveryRepository;
import org.trackitall.trackitall.delivery.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;

    @Transactional
    @Override
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO dto) {
        deliveryRepository.findByOrderId(dto.getOrderId())
                .ifPresent(d -> { throw new ValidationException("Une livraison existe déjà pour cette commande"); });

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Commande introuvable"));

        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setOrder(order);
        delivery.setStatus(DeliveryStatus.EN_COURS);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toResponseDTO(saved);
    }

    @Transactional
    @Override
    public DeliveryResponseDTO updateDelivery(Long id, DeliveryRequestDTO dto) {

        Delivery existing = deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livraison introuvable"));

        if (!existing.getOrder().getId().equals(dto.getOrderId())) {
            deliveryRepository.findByOrderId(dto.getOrderId())
                    .ifPresent(d -> { throw new ValidationException("Une livraison existe déjà pour cette commande"); });
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Commande introuvable"));

        deliveryMapper.updateEntityFromDTO(dto, existing);
        existing.setOrder(order);

        Delivery updated = deliveryRepository.save(existing);
        return deliveryMapper.toResponseDTO(updated);
    }


    @Transactional
    @Override
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livraison introuvable"));

        if (delivery.getStatus() == DeliveryStatus.LIVREE) {
            throw new ValidationException("Impossible de supprimer une livraison déjà livrée");
        }

        deliveryRepository.delete(delivery);
    }


    @Transactional(readOnly = true)
    @Override
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livraison introuvable"));
        return deliveryMapper.toResponseDTO(delivery);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(deliveryMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status) {

        return deliveryRepository.findByStatus(status).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public DeliveryResponseDTO updateDeliveryStatus(Long id, DeliveryStatus status) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livraison introuvable"));



        delivery.setStatus(status);

        if (status == DeliveryStatus.LIVREE) {
            delivery.setDeliveryDate(LocalDate.now());
        }

        Delivery updated = deliveryRepository.save(delivery);
        return deliveryMapper.toResponseDTO(updated);
    }


}
