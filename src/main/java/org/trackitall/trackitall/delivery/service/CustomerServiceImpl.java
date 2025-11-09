package org.trackitall.trackitall.delivery.service;

import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.delivery.mapper.CustomerMapper;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import org.trackitall.trackitall.delivery.service.ICustomerService;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;
import org.trackitall.trackitall.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        try {

            validateCustomerRequest(customerDTO);

            Customer customer = customerMapper.toEntity(customerDTO);
            Customer savedCustomer = customerRepository.save(customer);

            CustomerResponseDTO response = customerMapper.toResponseDTO(savedCustomer);
            response.setActiveOrdersCount(0);

            return response;

        } catch (ValidationException | BusinessException e) {

            throw e;
        } catch (Exception e) {

            throw new BusinessException("Erreur lors de la création du client: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO) {
        try {

            validateCustomerRequest(customerDTO);

            Customer existingCustomer = customerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

            customerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
            Customer updatedCustomer = customerRepository.save(existingCustomer);

            CustomerResponseDTO response = customerMapper.toResponseDTO(updatedCustomer);
            response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));
            return response;

        } catch (NotFoundException | ValidationException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la mise à jour du client: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        try {

            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

            Integer activeOrdersCount = customerRepository.countActiveOrdersByCustomerId(id);
            if (activeOrdersCount > 0) {
                throw new ValidationException("Impossible de supprimer le client car il a " + activeOrdersCount + " commande(s) active(s)");
            }

            customerRepository.delete(customer);

        } catch (NotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la suppression du client: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        try {
            return customerRepository.findAll(pageable)
                    .map(customer -> {
                        CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
                        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(customer.getId()));
                        return response;
                    });
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération des clients: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomersByName(String name, Pageable pageable) {
        try {

            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Le terme de recherche ne peut pas être vide");
            }

            return customerRepository.findByNameContainingIgnoreCase(name.trim(), pageable)
                    .map(customer -> {
                        CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
                        response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(customer.getId()));
                        return response;
                    });
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la recherche des clients: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Client non trouvé avec l'ID: " + id));

            CustomerResponseDTO response = customerMapper.toResponseDTO(customer);
            response.setActiveOrdersCount(customerRepository.countActiveOrdersByCustomerId(id));

            return response;

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la récupération du client: " + e.getMessage());
        }
    }

    private void validateCustomerRequest(CustomerRequestDTO customerDTO) {
        if (customerDTO == null) {
            throw new ValidationException("Les données du client ne peuvent pas être nulles");
        }
        if (customerDTO.getName() == null || customerDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Le nom du client est obligatoire");
        }


    }

}