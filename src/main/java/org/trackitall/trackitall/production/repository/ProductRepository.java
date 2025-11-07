package org.trackitall.trackitall.production.repository;

import org.trackitall.trackitall.production.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);
    Optional<Product> findByReference(String reference);

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByReferenceContainingIgnoreCase(String reference);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:searchTerm% OR p.reference LIKE %:searchTerm%")
    Page<Product> findByNameOrReferenceContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.id = :productId AND po.status != 'COMPLETED'")
    Integer countActiveProductionOrdersByProductId(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE SIZE(p.productionOrders) = 0 AND SIZE(p.orders) = 0")
    List<Product> findProductsWithoutAssociations();
}