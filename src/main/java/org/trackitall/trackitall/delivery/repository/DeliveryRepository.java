package org.trackitall.trackitall.delivery.repository;

import org.trackitall.trackitall.delivery.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trackitall.trackitall.enums.DeliveryStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Page<Delivery> findAll(Pageable pageable);

    List<Delivery> findByStatus(DeliveryStatus status);

    @Query("SELECT d FROM Delivery d WHERE d.order.id = :orderId")
    Optional<Delivery> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT d FROM Delivery d WHERE d.driver = :driverName")
    List<Delivery> findByDriver(@Param("driverName") String driverName);
}