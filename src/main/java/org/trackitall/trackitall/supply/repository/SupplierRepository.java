package org.trackitall.trackitall.supply.repository;

import org.trackitall.trackitall.supply.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Page<Supplier> findAll(Pageable pageable);

    Optional<Supplier> findByName(String name);

    @Query("SELECT s FROM Supplier s WHERE s.name LIKE %:name%")
    Page<Supplier> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT COUNT(so) > 0 FROM SupplyOrder so WHERE so.supplier.id = :supplierId AND so.status <> 'RECEIVED'")
    boolean hasActiveOrders(Long supplierId);
}