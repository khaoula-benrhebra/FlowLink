package org.supplychain.supplychain.service.Production;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.exception.InsufficientStockException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Production.ProductionOrderMapper;
import org.supplychain.supplychain.model.BillOfMaterial;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.ProductionOrder;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.Production.ProductRepository;
import org.supplychain.supplychain.repository.Production.ProductionOrderRepository;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    @Transactional
    public ProductionOrderDTO createProductionOrder(ProductionOrderDTO dto) {
        // 1. Vérifier que le produit existe
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé avec l'ID : " + dto.getProductId()));

        // 2. Vérifier si le stock du produit fini est suffisant
        Integer stockProduit = product.getStock() != null ? product.getStock() : 0;
        Integer quantiteDemandee = dto.getQuantity();

        ProductionOrder productionOrder = productionOrderMapper.toEntity(dto);
        productionOrder.setProduct(product);

        // Définir la priorité par défaut
        if (productionOrder.getPriority() == null) {
            productionOrder.setPriority(Priority.STANDARD);
        }

        // CAS 1 : Le stock du produit fini est suffisant
        if (stockProduit >= quantiteDemandee) {
            // Pas besoin de produire, on déduit directement du stock
            product.setStock(stockProduit - quantiteDemandee);
            productRepository.save(product);

            // Statut = TERMINE
            productionOrder.setStatus(ProductionOrderStatus.TERMINE);

            // Dates automatiques (commande déjà terminée)
            productionOrder.setStartDate(LocalDate.now());
            productionOrder.setEndDate(LocalDate.now());

            ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);
            return productionOrderMapper.toDTO(savedOrder);
        }

        // CAS 2 : Le stock du produit fini est insuffisant
        // Calculer combien il faut vraiment produire
        Integer quantiteAProduire = quantiteDemandee - stockProduit;

        // 3. Vérifier la disponibilité des matières premières
        List<BillOfMaterial> bom = product.getBillOfMaterials();

        if (bom == null || bom.isEmpty()) {
            throw new IllegalStateException(
                    "Impossible de créer l'ordre de production. Le produit n'a pas de nomenclature (BOM) définie."
            );
        }

        // Vérifier chaque matière première
        for (BillOfMaterial bomItem : bom) {
            RawMaterial material = bomItem.getMaterial();
            Integer quantiteNecessaire = bomItem.getQuantity() * quantiteAProduire;
            Integer stockDisponible = material.getStock() != null ? material.getStock() : 0;

            // Si une matière est insuffisante, on arrête immédiatement
            if (stockDisponible < quantiteNecessaire) {
                // Créer l'ordre avec statut EN_ATTENTE
                productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);

                // Calculer les dates estimées
                if (productionOrder.getStartDate() != null) {
                    int estimatedDays = quantiteAProduire * product.getProductionTime();
                    productionOrder.setEndDate(productionOrder.getStartDate().plusDays(estimatedDays));
                }

                // Sauvegarder l'ordre
                productionOrderRepository.save(productionOrder);

                // Lever l'exception
                throw new InsufficientStockException(
                        "Stock insuffisant pour " + material.getName() +
                                " - Nécessaire: " + quantiteNecessaire + " " + material.getUnit() +
                                ", Disponible: " + stockDisponible + " " + material.getUnit()
                );
            }
        }

        // CAS 2.B : Stock de matières premières suffisant
        // Déduire le stock de chaque matière première
        for (BillOfMaterial bomItem : bom) {
            RawMaterial material = bomItem.getMaterial();
            Integer quantiteNecessaire = bomItem.getQuantity() * quantiteAProduire;

            material.setStock(material.getStock() - quantiteNecessaire);
            rawMaterialRepository.save(material);
        }

        // Statut = EN_PRODUCTION
        productionOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);

        // Calculer les dates de production
        if (productionOrder.getStartDate() == null) {
            productionOrder.setStartDate(LocalDate.now());
        }

        int estimatedDays = quantiteAProduire * product.getProductionTime();
        productionOrder.setEndDate(productionOrder.getStartDate().plusDays(estimatedDays));

        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public ProductionOrderDTO updateProductionOrder(Long id, ProductionOrderDTO dto) {
        ProductionOrder existingOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordre de production non trouvé avec l'ID : " + id));

        // Si le produit est modifié, vérifier qu'il existe
        if (dto.getProductId() != null &&
                !dto.getProductId().equals(existingOrder.getProduct().getIdProduct())) {

            Product newProduct = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produit non trouvé avec l'ID : " + dto.getProductId()));

            existingOrder.setProduct(newProduct);
        }

        // Mettre à jour les champs
        productionOrderMapper.updateEntityFromDTO(dto, existingOrder);

        // Recalculer le temps estimé si la quantité ou la date de début change
        if (dto.getQuantity() != null || dto.getStartDate() != null) {
            LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : existingOrder.getStartDate();
            if (startDate != null) {
                int estimatedDays = existingOrder.getQuantity() * existingOrder.getProduct().getProductionTime();
                existingOrder.setEndDate(startDate.plusDays(estimatedDays));
            }
        }

        ProductionOrder updatedOrder = productionOrderRepository.save(existingOrder);

        return productionOrderMapper.toDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelProductionOrder(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordre de production non trouvé avec l'ID : " + id));

        // Vérifier que l'ordre est en attente
        if (order.getStatus() != ProductionOrderStatus.EN_ATTENTE) {
            throw new ResourceInUseException(
                    "Impossible d'annuler l'ordre. Seuls les ordres avec le statut EN_ATTENTE peuvent être annulés. " +
                            "Statut actuel : " + order.getStatus()
            );
        }

        productionOrderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductionOrderDTO> getAllProductionOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable)
                .map(productionOrderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductionOrderDTO> getProductionOrdersByStatus(
            ProductionOrderStatus status,
            Pageable pageable) {

        return productionOrderRepository.findByStatus(status, pageable)
                .map(productionOrderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionOrderDTO getProductionOrderById(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordre de production non trouvé avec l'ID : " + id));

        return productionOrderMapper.toDTO(order);
    }
}