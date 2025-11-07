package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ICustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO);
    void deleteCustomer(Long id);
    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);
    Page<CustomerResponseDTO> searchCustomersByName(String name, Pageable pageable);
    CustomerResponseDTO getCustomerById(Long id);
}