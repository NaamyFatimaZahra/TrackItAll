package org.trackitall.trackitall.delivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.trackitall.trackitall.enums.DeliveryStatus;
import org.trackitall.trackitall.delivery.entity.Order;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotBlank
    @Column(nullable = false)
    private String vehicule;

    @NotBlank
    @Column(nullable = false)
    private String driver;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    private LocalDate deliveryDate;

    @NotNull
    @Column(nullable = false)
    private Double cost;
}