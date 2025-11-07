package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer productionTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Integer minimumStock = 0;

    @Column(nullable = false)
    private String unit;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<BillOfMaterial> billOfMaterials = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductionOrder> productionOrders = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOrder> productOrders = new ArrayList<>();




}