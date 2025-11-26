package org.trackitall.trackitall.delivery.repository;

import org.trackitall.trackitall.delivery.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trackitall.trackitall.enums.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAll(Pageable pageable);

    List<Order> findByStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId AND o.status != 'DELIVERED'")
    Integer countActiveOrdersByCustomerId(@Param("customerId") Long customerId);
}