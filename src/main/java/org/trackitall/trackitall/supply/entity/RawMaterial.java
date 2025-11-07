package org.trackitall.trackitall.supply.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "raw_materials")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int stockMin;

    @NotBlank
    @Column(nullable = false)
    private String unit;


    @ManyToMany(mappedBy = "rawMaterials")
    private List<Supplier> suppliers;


    @ManyToMany(mappedBy = "rawMaterials")
    private List<SupplyOrder> supplyOrders;
}
