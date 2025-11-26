package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.trackitall.trackitall.enums.OrderStatus;

import java.util.List;

public interface IOrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderDTO);
    OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderDTO);
    void cancelOrder(Long id);
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatus status);
}