package org.supplychain.supplychain.service.approvisionnement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.*;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.exception.*;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderMapper;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderLineMapper;
import org.supplychain.supplychain.model.*;
import org.supplychain.supplychain.repository.approvisionnement.*;
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
    public OrderResponse createSupplyOrder(OrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
        SupplyOrder order = new SupplyOrder();
        order.setSupplier(supplier);
        order.setOrderDate(request.getOrderDate());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setOrderLines(new ArrayList<>());

        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        List<SupplyOrderLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderLineRequest req : request.getOrderLines()) {
            RawMaterial mat = rawMaterialRepository.findById(req.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material", "id", req.getRawMaterialId()));
            SupplyOrderLine line = new SupplyOrderLine();
            line.setSupplyOrder(savedOrder);
            line.setRawMaterial(mat);
            line.setQuantity(req.getQuantity());
            line.setUnitPrice(req.getUnitPrice());
            lines.add(line);
            total = total.add(req.getUnitPrice().multiply(BigDecimal.valueOf(req.getQuantity())));
        }
        supplyOrderLineRepository.saveAll(lines);
        savedOrder.setOrderLines(lines);
        savedOrder.setTotalAmount(total);
        return supplyOrderMapper.toResponse(supplyOrderRepository.save(savedOrder));
    }
    @Override
    public OrderResponse updateSupplyOrder(Long id, OrderRequest request) {
        SupplyOrder order = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));

        if (order.getStatus() == SupplyOrderStatus.RECUE) {
            throw new ResourceInUseException("Impossible de modifier une commande déjà reçue.");
        }
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
        order.setSupplier(supplier);
        order.setOrderDate(request.getOrderDate());

        supplyOrderLineRepository.deleteAll(order.getOrderLines());
        order.getOrderLines().clear();

        BigDecimal total = BigDecimal.ZERO;
        List<SupplyOrderLine> newLines = new ArrayList<>();
        for (OrderLineRequest req : request.getOrderLines()) {
            RawMaterial mat = rawMaterialRepository.findById(req.getRawMaterialId()).orElseThrow();
            SupplyOrderLine line = new SupplyOrderLine();
            line.setSupplyOrder(order);
            line.setRawMaterial(mat);
            line.setQuantity(req.getQuantity());
            line.setUnitPrice(req.getUnitPrice());
            newLines.add(line);
            total = total.add(req.getUnitPrice().multiply(BigDecimal.valueOf(req.getQuantity())));
        }
        order.getOrderLines().addAll(newLines);
        order.setTotalAmount(total);
        return supplyOrderMapper.toResponse(supplyOrderRepository.save(order));
    }
    @Override
    public void deleteSupplyOrder(Long id) {
        SupplyOrder order = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));
        if (order.getStatus() == SupplyOrderStatus.RECUE) {
            throw new ResourceInUseException("Impossible de supprimer une commande reçue.");
        }
        supplyOrderRepository.delete(order);
    }
    @Override @Transactional(readOnly = true)
    public Page<OrderResponse> getAllSupplyOrders(Pageable pageable) {
        return supplyOrderRepository.findAll(pageable).map(supplyOrderMapper::toResponse);
    }
    @Override @Transactional(readOnly = true)
    public Page<OrderResponse> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable) {
        return supplyOrderRepository.findByStatus(status, pageable).map(supplyOrderMapper::toResponse);
    }
    @Override @Transactional(readOnly = true)
    public OrderResponse getSupplyOrderById(Long id) {
        return supplyOrderRepository.findById(id)
                .map(supplyOrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyOrder", "id", id));
    }
    @Override @Transactional(readOnly = true)
    public List<OrderLineResponse> getOrderLines(Long orderId) {
        if (!supplyOrderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("SupplyOrder", "id", orderId);
        }
        return supplyOrderLineMapper.toResponseList(supplyOrderLineRepository.findBySupplyOrder_IdOrder(orderId));
    }
}