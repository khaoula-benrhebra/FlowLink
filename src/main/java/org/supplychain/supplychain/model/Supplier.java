package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupplier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String email;

    private String phone;

    private Double rating;

    private Integer leadTime;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<SupplyOrder> supplyOrders = new ArrayList<>();

    @ManyToMany(mappedBy = "suppliers")
    private List<RawMaterial> materials = new ArrayList<>();


}
