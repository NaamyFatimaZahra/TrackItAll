package org.trackitall.trackitall.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.trackitall.trackitall.delivery.entity.Order;

import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer productionTime;

    @NotNull
    @Column(nullable = false)
    private Double cost;

    @NotNull
    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BillOfMaterial> billOfMaterials;

    @OneToMany(mappedBy = "product")
    private List<ProductionOrder> productionOrders;

    @OneToMany(mappedBy = "product")
    private List<Order> orders;
}