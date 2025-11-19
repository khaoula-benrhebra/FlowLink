package org.supplychain.supplychain.service.approvisionnement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderDTO;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderLineDTO;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderLineMapper;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.model.SupplyOrder;
import org.supplychain.supplychain.model.SupplyOrderLine;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderLineRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplyOrderServiceImpl implements SupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplyOrderLineRepository supplyOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderMapper supplyOrderMapper;
    private final SupplyOrderLineMapper supplyOrderLineMapper;


    @Override
    public SupplyOrderDTO createSupplyOrder(SupplyOrderDTO supplyOrderDTO) {

        Supplier supplier = supplierRepository.findById(supplyOrderDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplyOrderDTO.getSupplierId()));

        SupplyOrder supplyOrder = supplyOrderMapper.toEntity(supplyOrderDTO);
        supplyOrder.setSupplier(supplier);

        List<SupplyOrderLine> orderLines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        SupplyOrder savedOrder = supplyOrderRepository.save(supplyOrder);

        for (SupplyOrderLineDTO lineDTO : supplyOrderDTO.getOrderLines()) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(lineDTO.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", lineDTO.getRawMaterialId()));

            SupplyOrderLine orderLine = supplyOrderLineMapper.toEntity(lineDTO);
            orderLine.setSupplyOrder(savedOrder);
            orderLine.setRawMaterial(rawMaterial);

            orderLines.add(orderLine);

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        supplyOrderLineRepository.saveAll(orderLines);
        savedOrder.setTotalAmount(totalAmount);
        SupplyOrder updatedOrder = supplyOrderRepository.save(savedOrder);

        SupplyOrderDTO result = supplyOrderMapper.toDTO(updatedOrder);
        result.setOrderLines(supplyOrderLineMapper.toDTOList(updatedOrder.getOrderLines()));
        return result;
    }

    @Override
    public SupplyOrderDTO updateSupplyOrder(Long id, SupplyOrderDTO supplyOrderDTO) {
        SupplyOrder existingOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));


        Supplier supplier = supplierRepository.findById(supplyOrderDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplyOrderDTO.getSupplierId()));

        existingOrder.setSupplier(supplier);
        existingOrder.setOrderDate(supplyOrderDTO.getOrderDate());
        existingOrder.setStatus(supplyOrderDTO.getStatus());

        // Supprimer les anciennes lignes via orphanRemoval (vider la collection)
        existingOrder.getOrderLines().clear();

        List<SupplyOrderLine> newOrderLines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SupplyOrderLineDTO lineDTO : supplyOrderDTO.getOrderLines()) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(lineDTO.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", lineDTO.getRawMaterialId()));

            SupplyOrderLine orderLine = supplyOrderLineMapper.toEntity(lineDTO);
            orderLine.setSupplyOrder(existingOrder);
            orderLine.setRawMaterial(rawMaterial);

            newOrderLines.add(orderLine);

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        // Ajouter les nouvelles lignes à la collection
        existingOrder.getOrderLines().addAll(newOrderLines);
        existingOrder.setTotalAmount(totalAmount);

        SupplyOrder updatedOrder = supplyOrderRepository.save(existingOrder);

        SupplyOrderDTO result = supplyOrderMapper.toDTO(updatedOrder);
        result.setOrderLines(supplyOrderLineMapper.toDTOList(updatedOrder.getOrderLines()));
        return result;
    }


    @Override
    public void deleteSupplyOrder(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));

        if (supplyOrder.getStatus() == SupplyOrderStatus.RECUE) {
            throw new ResourceInUseException("Cannot delete a delivered supply order with ID: " + id);
        }

        supplyOrderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyOrderDTO> getAllSupplyOrders(Pageable pageable) {
        Page<SupplyOrder> supplyOrders = supplyOrderRepository.findAll(pageable);
        return supplyOrders.map(order -> {
            SupplyOrderDTO dto = supplyOrderMapper.toDTO(order);
            dto.setOrderLines(supplyOrderLineMapper.toDTOList(order.getOrderLines()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyOrderDTO> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable) {
        Page<SupplyOrder> supplyOrders = supplyOrderRepository.findByStatus(status, pageable);
        return supplyOrders.map(order -> {
            SupplyOrderDTO dto = supplyOrderMapper.toDTO(order);
            dto.setOrderLines(supplyOrderLineMapper.toDTOList(order.getOrderLines()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public SupplyOrderDTO getSupplyOrderById(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));
        SupplyOrderDTO dto = supplyOrderMapper.toDTO(supplyOrder);
        dto.setOrderLines(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines()));
        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<SupplyOrderLineDTO> getOrderLines(Long orderId) {
        if (!supplyOrderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("SupplyOrder", "id", orderId);
        }

        List<SupplyOrderLine> orderLines = supplyOrderLineRepository.findBySupplyOrder_IdOrder(orderId);
        return supplyOrderLineMapper.toDTOList(orderLines);
    }
}