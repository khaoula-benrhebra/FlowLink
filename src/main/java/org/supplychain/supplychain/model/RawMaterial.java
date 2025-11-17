package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.model.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raw_materials")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaterial;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer stock = 0;


    @Column(nullable = false)
    private Integer stockMin;

    @Column(nullable = false)
    private String unit;


    @ManyToMany
    @JoinTable(
            name = "material_suppliers",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers = new ArrayList<>();

    @OneToMany(mappedBy = "rawMaterial")
    private List<SupplyOrderLine> supplyOrderLines = new ArrayList<>();

    @OneToMany(mappedBy = "material")
    private List<BillOfMaterial> billOfMaterials = new ArrayList<>();


}