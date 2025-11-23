package org.trackitall.trackitall.supply.repository;

import org.trackitall.trackitall.supply.entity.SupplyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {

    Page<SupplyOrder> findAll(Pageable pageable);
}