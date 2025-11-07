package org.trackitall.trackitall.supply.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "supply_order_raw_materials")
public class SupplyOrderRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "supply_order_id")
    private SupplyOrder supplyOrder;

    @ManyToOne
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;


    private int quantity;

}
