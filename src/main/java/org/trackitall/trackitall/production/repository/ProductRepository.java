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


    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.billOfMaterials b LEFT JOIN FETCH b.material WHERE p.id = :id")
    Optional<Product> findByIdWithBillOfMaterials(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:searchTerm% ")
    Page<Product> findByNameContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.id = :productId AND po.status != 'COMPLETED'")
    Integer countActiveProductionOrdersByProductId(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE SIZE(p.productionOrders) = 0 AND SIZE(p.orders) = 0")
    List<Product> findProductsWithoutAssociations();
}