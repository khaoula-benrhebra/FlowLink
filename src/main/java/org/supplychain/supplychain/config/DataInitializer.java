package org.supplychain.supplychain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.model.*;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
//import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (supplierRepository.count() > 0) {
            log.info("Database already contains data. Skipping initialization.");
            return;
        }

        log.info("Starting database initialization with fake data...");

        createSuppliers();
        createRawMaterials();

        log.info("Database initialization completed successfully!");
    }

    private void createSuppliers() {
        log.info("Creating suppliers...");

        List<Supplier> suppliers = List.of(
                createSupplier("Global Steel Corp", "Ahmed Benjelloun", "ahmed@globalsteel.ma", "+212600111222", 4.8, 3),
                createSupplier("TechMaterials Ltd", "Sarah Johnson", "sarah@techmaterials.com", "+212600222333", 4.5, 5),
                createSupplier("Atlas Components", "Mohammed El Alami", "m.elalami@atlascomp.ma", "+212600333444", 4.2, 7),
                createSupplier("EuroSupply GmbH", "Hans Mueller", "hans@eurosupply.de", "+49301234567", 4.6, 10),
                createSupplier("MedTech Industries", "Fatima Zahra", "fatima@medtech.ma", "+212600444555", 4.3, 4),
                createSupplier("Oriental Trading Co", "Li Wei", "liwei@orientaltrade.cn", "+86123456789", 3.9, 15),
                createSupplier("African Resources", "John Mwangi", "john@africanres.ke", "+254712345678", 4.1, 8),
                createSupplier("FastDelivery SARL", "Pierre Dubois", "pierre@fastdelivery.fr", "+33612345678", 4.7, 2),
                createSupplier("Quality Metals Inc", "David Smith", "david@qualitymetals.us", "+1234567890", 4.4, 6),
                createSupplier("Casablanca Supplies", "Youssef Idrissi", "youssef@casasupplies.ma", "+212600555666", 4.0, 5)
        );

        supplierRepository.saveAll(suppliers);
        log.info("Created {} suppliers", suppliers.size());
    }

    private Supplier createSupplier(String name, String contact, String email, String phone, double rating, int leadTime) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContact(contact);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setRating(rating);
        supplier.setLeadTime(leadTime);
        return supplier;
    }

    private void createRawMaterials() {
        log.info("Creating raw materials...");

        List<Supplier> suppliers = supplierRepository.findAll();

        List<RawMaterial> materials = new ArrayList<>();

        materials.add(createRawMaterial(
                "Steel Sheet A36",
                "High-strength low-alloy structural steel",
                500, 100, "kg",
                suppliers.subList(0, 3)
        ));

        materials.add(createRawMaterial(
                "Aluminum 6061",
                "Heat treatable aluminum alloy",
                300, 80, "kg",
                suppliers.subList(1, 4)
        ));

        materials.add(createRawMaterial(
                "Copper Wire 10AWG",
                "Electrical grade copper wire",
                15, 50, "kg",
                suppliers.subList(2, 5)
        ));

        materials.add(createRawMaterial(
                "Stainless Steel 304",
                "Corrosion-resistant stainless steel",
                200, 60, "kg",
                suppliers.subList(0, 2)
        ));

        materials.add(createRawMaterial(
                "Plastic Resin PVC",
                "Polyvinyl chloride resin for molding",
                800, 200, "kg",
                suppliers.subList(3, 6)
        ));

        materials.add(createRawMaterial(
                "Rubber Gasket Material",
                "EPDM rubber for sealing applications",
                120, 40, "meters",
                suppliers.subList(4, 7)
        ));

        materials.add(createRawMaterial(
                "Carbon Fiber Fabric",
                "High-strength composite material",
                50, 20, "meters",
                suppliers.subList(5, 8)
        ));

        materials.add(createRawMaterial(
                "Titanium Grade 5",
                "Ti-6Al-4V aerospace grade titanium",
                30, 10, "kg",
                suppliers.subList(1, 3)
        ));

        materials.add(createRawMaterial(
                "Brass Rod C360",
                "Free-cutting brass for machining",
                180, 50, "kg",
                suppliers.subList(0, 4)
        ));

        materials.add(createRawMaterial(
                "Silicone Rubber",
                "High-temperature resistant silicone",
                90, 30, "kg",
                suppliers.subList(6, 9)
        ));

        materials.add(createRawMaterial(
                "Glass Fiber Cloth",
                "E-glass woven fabric for composites",
                60, 25, "meters",
                suppliers.subList(2, 5)
        ));

        materials.add(createRawMaterial(
                "Zinc Plating Compound",
                "Electroplating zinc for corrosion protection",
                40, 15, "liters",
                suppliers.subList(7, 10)
        ));

        materials.add(createRawMaterial(
                "Epoxy Resin",
                "Two-component epoxy adhesive system",
                70, 25, "liters",
                suppliers.subList(3, 6)
        ));

        materials.add(createRawMaterial(
                "Nylon PA6",
                "Polyamide 6 engineering plastic",
                250, 75, "kg",
                suppliers.subList(4, 7)
        ));

        materials.add(createRawMaterial(
                "Bronze Bearing Material",
                "Oil-impregnated bronze for bearings",
                35, 15, "kg",
                suppliers.subList(0, 3)
        ));

        materials.add(createRawMaterial(
                "Hardened Steel Pins",
                "Heat-treated steel dowel pins",
                5000, 1000, "pieces",
                suppliers.subList(1, 4)
        ));

        materials.add(createRawMaterial(
                "Industrial Lubricant",
                "Synthetic high-performance lubricating oil",
                45, 20, "liters",
                suppliers.subList(5, 8)
        ));

        materials.add(createRawMaterial(
                "Ceramic Insulation",
                "High-temperature ceramic fiber insulation",
                80, 30, "meters",
                suppliers.subList(6, 9)
        ));

        materials.add(createRawMaterial(
                "Magnesium Alloy AZ31",
                "Lightweight structural magnesium",
                18, 8, "kg",
                suppliers.subList(2, 5)
        ));

        materials.add(createRawMaterial(
                "Nickel Plating Solution",
                "Electroless nickel plating bath",
                25, 10, "liters",
                suppliers.subList(7, 10)
        ));

        for (RawMaterial material : materials) {
            material.setReservedStock(random.nextInt(material.getStock() / 4));
            material.setLastRestockDate(LocalDate.now().minusDays(random.nextInt(60)));
        }

        rawMaterialRepository.saveAll(materials);
        log.info("Created {} raw materials", materials.size());
    }

    private RawMaterial createRawMaterial(String name, String description, int stock,
                                          int stockMin, String unit, List<Supplier> suppliers) {
        RawMaterial material = new RawMaterial();
        material.setName(name);
        material.setDescription(description);
        material.setStock(stock);
        material.setStockMin(stockMin);
        material.setUnit(unit);
        material.setReservedStock(0);
        material.setSuppliers(new ArrayList<>(suppliers));
        return material;
    }
}