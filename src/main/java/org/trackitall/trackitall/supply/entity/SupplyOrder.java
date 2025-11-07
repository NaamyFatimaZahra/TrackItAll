package org.trackitall.trackitall.supply.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.trackitall.trackitall.enums.SupplyOrderStatus;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "supply_orders")
public class SupplyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyOrderStatus status;


    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;


    @ManyToMany
    @JoinTable(
            name = "supply_order_raw_materials",
            joinColumns = @JoinColumn(name = "supply_order_id"),
            inverseJoinColumns = @JoinColumn(name = "raw_material_id")
    )
    private List<RawMaterial> rawMaterials;
}
