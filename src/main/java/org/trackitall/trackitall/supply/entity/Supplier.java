package org.trackitall.trackitall.supply.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String contact;

    private Double rating;

    @Column(nullable = false)
    private int leadTime;


    @ManyToMany
    @JoinTable(
            name = "supplier_raw_materials",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "raw_material_id")
    )
    private List<RawMaterial> rawMaterials;
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<SupplyOrder> supplyOrders;
}
