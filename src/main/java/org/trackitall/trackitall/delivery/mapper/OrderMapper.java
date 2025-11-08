package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "EN_PREPARATION")
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "product", source = "productId")
    public abstract Order toEntity(OrderRequestDTO dto);

    public abstract OrderResponseDTO toResponseDTO(Order entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "product", source = "productId")
    public abstract void updateEntityFromDTO(OrderRequestDTO dto, @MappingTarget Order entity);

    protected Customer mapCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    protected Product mapProduct(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}