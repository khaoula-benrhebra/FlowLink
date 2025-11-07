package org.supplychain.supplychain.service.Production.ProductionOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Production.ProductionOrderMapper;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.ProductionOrder;
import org.supplychain.supplychain.repository.Production.ProductRepository;
import org.supplychain.supplychain.repository.Production.ProductionOrderRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    @Transactional
    public ProductionOrderDTO createProductionOrder(ProductionOrderDTO dto) {
        // Vérifier que le produit existe
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé avec l'ID : " + dto.getProductId()));

        // Créer l'ordre de production
        ProductionOrder productionOrder = productionOrderMapper.toEntity(dto);
        productionOrder.setProduct(product);

        // Définir le statut
        if (productionOrder.getStatus() == null) {
            productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        }

        // Définir la priorité par défaut
        if (productionOrder.getPriority() == null) {
            productionOrder.setPriority(Priority.STANDARD);
        }

        // Calculer le temps estimé de production
        // endDate = quantity * productionTime (en jours)
        if (productionOrder.getStartDate() != null) {
            int estimatedDays = dto.getQuantity() * product.getProductionTime();
            productionOrder.setEndDate(productionOrder.getStartDate().plusDays(estimatedDays));
        }

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