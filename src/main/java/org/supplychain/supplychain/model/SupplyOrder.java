package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supply_orders")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "supplyOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyOrderLine> orderLines = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate orderDate;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyOrderStatus status = SupplyOrderStatus.EN_ATTENTE;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;


}