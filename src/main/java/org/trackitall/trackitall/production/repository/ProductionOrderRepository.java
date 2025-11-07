package org.trackitall.trackitall.production.repository;

import org.trackitall.trackitall.production.entity.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Page<ProductionOrder> findAll(Pageable pageable);

    List<ProductionOrder> findByStatus(String status);

    @Query("SELECT po FROM ProductionOrder po WHERE po.product.id = :productId")
    List<ProductionOrder> findByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.id = :productId AND po.status != 'COMPLETED'")
    Integer countActiveProductionOrdersByProductId(@Param("productId") Long productId);
}