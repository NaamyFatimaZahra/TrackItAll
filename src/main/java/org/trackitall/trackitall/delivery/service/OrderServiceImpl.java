package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.mapper.DeliveryMapper;
import org.trackitall.trackitall.delivery.mapper.OrderMapper;
import org.trackitall.trackitall.delivery.repository.OrderRepository;
import org.trackitall.trackitall.delivery.repository.DeliveryRepository;
import org.trackitall.trackitall.delivery.service.IOrderService;
import org.trackitall.trackitall.enums.OrderStatus;
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
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderMapper orderMapper;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderDTO) {
        try {
            Order order = orderMapper.toEntity(orderDTO);
            Order savedOrder = orderRepository.save(order);
            return orderMapper.toResponseDTO(savedOrder);
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la création de la commande: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderDTO) {
        try {
            Order existingOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

            if (!OrderStatus.EN_PREPARATION.equals(existingOrder.getStatus())) {
                throw new ValidationException("Impossible de modifier une commande déjà expédiée");
            }
            orderMapper.updateEntityFromDTO(orderDTO, existingOrder);
            Order updatedOrder = orderRepository.save(existingOrder);
            return orderMapper.toResponseDTO(updatedOrder);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour de la commande: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

            if (!OrderStatus.EN_PREPARATION.equals(order.getStatus())) {
                throw new ValidationException("Impossible d'annuler une commande déjà expédiée");
            }

            orderRepository.delete(order);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de l'annulation de la commande: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        try {
            return orderRepository.findAll(pageable)
                    .map(orderMapper::toResponseDTO);
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));
            return orderMapper.toResponseDTO(order);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération de la commande: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        try {

            try {
                org.trackitall.trackitall.enums.OrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Statut invalide: " + status);
            }

            return orderRepository.findByStatus(status).stream()
                    .map(orderMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des commandes par statut: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

            try {
                order.setStatus(org.trackitall.trackitall.enums.OrderStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Statut invalide: " + status);
            }

            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toResponseDTO(updatedOrder);
        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour du statut de la commande: " + e.getMessage());
        }
    }


}