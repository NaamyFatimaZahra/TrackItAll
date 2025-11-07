package org.trackitall.trackitall.delivery.repository;

import org.trackitall.trackitall.delivery.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByName(String name);

    List<Customer> findByNameContainingIgnoreCase(String name);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId AND o.status != 'DELIVERED'")
    Integer countActiveOrdersByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM Customer c WHERE SIZE(c.orders) = 0")
    List<Customer> findCustomersWithoutOrders();
}