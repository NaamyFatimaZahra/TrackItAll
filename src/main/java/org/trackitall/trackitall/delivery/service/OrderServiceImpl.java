package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.mapper.OrderMapper;
import org.trackitall.trackitall.delivery.repository.OrderRepository;
import org.trackitall.trackitall.delivery.repository.DeliveryRepository;
import org.trackitall.trackitall.delivery.service.IOrderService;
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

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);
        return enrichOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        if (!"PREPARING".equals(existingOrder.getStatus().name())) {
            throw new RuntimeException("Impossible de modifier une commande déjà expédiée");
        }

        orderMapper.updateEntityFromDTO(orderDTO, existingOrder);
        Order updatedOrder = orderRepository.save(existingOrder);
        return enrichOrderResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        if (!"PREPARING".equals(order.getStatus().name())) {
            throw new RuntimeException("Impossible d'annuler une commande déjà expédiée");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::enrichOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));
        return enrichOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::enrichOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        order.setStatus(org.trackitall.trackitall.enums.OrderStatus.valueOf(status));
        Order updatedOrder = orderRepository.save(order);
        return enrichOrderResponse(updatedOrder);
    }

    private OrderResponseDTO enrichOrderResponse(Order order) {
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        // Ajouter les informations de livraison
        deliveryRepository.findByOrderId(order.getId())
                .ifPresent(delivery -> {
                    // Vous devrez créer un DeliveryMapper pour convertir l'entité Delivery en DTO
                    // response.setDelivery(deliveryMapper.toResponseDTO(delivery));
                });

        return response;
    }
}