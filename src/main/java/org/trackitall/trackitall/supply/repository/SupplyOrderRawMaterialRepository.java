package org.trackitall.trackitall.supply.repository;

import org.trackitall.trackitall.supply.entity.SupplyOrderRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplyOrderRawMaterialRepository extends JpaRepository<SupplyOrderRawMaterial, Long> {

    List<SupplyOrderRawMaterial> findBySupplyOrderId(Long supplyOrderId);

    void deleteBySupplyOrderId(Long supplyOrderId);
}