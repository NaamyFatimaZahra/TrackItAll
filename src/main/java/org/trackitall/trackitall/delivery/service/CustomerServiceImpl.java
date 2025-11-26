package org.trackitall.trackitall.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.delivery.mapper.CustomerMapper;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {


        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);

        CustomerResponseDTO response = customerMapper.toResponseDTO(savedCustomer);
        response.setActiveOrdersCount(0);
        return response;
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {


        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

        customerMapper.updateEntityFromDTO(dto, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        CustomerResponseDTO response = customerMapper.toResponseDTO(updatedCustomer);
        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));
        return response;
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

        int activeOrdersCount = customerRepository.countActiveOrdersByCustomerId(id);
        if (activeOrdersCount > 0) {
            throw new ValidationException("Impossible de supprimer le client car il a " + activeOrdersCount + " commande(s) active(s)");
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
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Le terme de recherche ne peut pas être vide");
        }

        return customerRepository.findByNameContainingIgnoreCase(name.trim(), pageable)
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
                .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

        CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));
        return response;
    }


}
