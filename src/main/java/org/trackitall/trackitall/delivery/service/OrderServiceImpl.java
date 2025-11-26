package org.trackitall.trackitall.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.mapper.OrderMapper;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import org.trackitall.trackitall.delivery.repository.OrderRepository;
import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.production.mapper.ProductMapper;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.trackitall.trackitall.production.service.IProductService;
import org.trackitall.trackitall.enums.OrderStatus;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final ProductRepository productRepository;
    private final IProductService productService;

private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId()).orElseThrow(
                ()->{
                    throw new ValidationException("no product with this id");
                }
        );


        product.setStock(product.getStock() - dto.getQuantity());

        Customer customerEntity = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + dto.getCustomerId()));

        Order order = orderMapper.toEntity(dto);
        order.setStatus(OrderStatus.EN_PREPARATION);
        order.setProduct(product);
        order.setCustomer(customerEntity);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

        if (!OrderStatus.EN_PREPARATION.equals(existingOrder.getStatus())) {
            throw new ValidationException("Impossible de modifier une commande déjà expédiée");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ValidationException("Produit introuvable avec cet ID"));

        int oldQuantity = existingOrder.getQuantity();
        int newQuantity = dto.getQuantity();
        int difference = newQuantity - oldQuantity;

        if (difference > 0 && product.getStock() < difference) {
            throw new ValidationException("Stock insuffisant pour augmenter la quantité");
        }

        product.setStock(product.getStock() - difference);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + dto.getCustomerId()));

        orderMapper.updateEntityFromDTO(dto, existingOrder);

        existingOrder.setProduct(product);
        existingOrder.setCustomer(customer);

        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toResponseDTO(updatedOrder);
    }


    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

        if (!OrderStatus.EN_PREPARATION.equals(order.getStatus())) {
            throw new ValidationException("Impossible d'annuler une commande déjà expédiée");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {


        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée avec l'ID: " + id));

            order.setStatus(status);


        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(updatedOrder);
    }


}
