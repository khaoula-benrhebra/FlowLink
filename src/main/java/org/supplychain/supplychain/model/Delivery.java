package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDelivery;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private String deliveryAddress;

    private String driver;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PLANIFIEE;

    private LocalDate DeliveryDate;


    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryCost;

}