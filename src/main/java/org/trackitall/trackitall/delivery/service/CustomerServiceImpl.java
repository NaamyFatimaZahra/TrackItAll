package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.delivery.mapper.OrderMapper;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final OrderMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        CustomerResponseDTO response = customerMapper.toResponseDTO(savedCustomer);
        response.setActiveOrdersCount(0);
        return response;
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));

        customerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        CustomerResponseDTO response = customerMapper.toResponseDTO(updatedCustomer);
        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));
        return response;
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));

        Integer activeOrdersCount = customerRepository.countActiveOrdersByCustomerId(id);
        if (activeOrdersCount > 0) {
            throw new RuntimeException("Impossible de supprimer le client car il a des commandes actives");
        }

        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customer -> {
                    CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
                    response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(customer.getId()));
                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomersByName(String name, Pageable pageable) {
        return customerRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(customer -> {
                    CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
                    response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(customer.getId()));
                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));
        CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));
        return response;
    }
}