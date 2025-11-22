package org.trackitall.trackitall.supply.repository;

import org.trackitall.trackitall.supply.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Page<RawMaterial> findAll(Pageable pageable);

    Optional<RawMaterial> findById(Long id);

    Optional<RawMaterial> findByName(String name);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock < rm.stockMin")
    List<RawMaterial> findLowStockMaterials();

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.name LIKE %:name%")
    Page<RawMaterial> findByNameContaining(String name, Pageable pageable);

    boolean existsByIdAndSupplyOrdersIsNotEmpty(Long id);
}