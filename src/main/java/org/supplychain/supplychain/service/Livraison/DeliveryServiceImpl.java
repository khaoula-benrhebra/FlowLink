package org.supplychain.supplychain.service.Livraison;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Livraison.DeliveryDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Livraison.DeliveryMapper;
import org.supplychain.supplychain.model.Delivery;
import org.supplychain.supplychain.model.Order;
import org.supplychain.supplychain.repository.Livraison.DeliveryRepository;
import org.supplychain.supplychain.repository.Livraison.OrderRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        Order order = orderRepository.findById(deliveryDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", deliveryDTO.getOrderId()));

        if (deliveryRepository.hasDeliveryForOrder(deliveryDTO.getOrderId())) {
            throw new DuplicateResourceException("Delivery already exists for order ID: " + deliveryDTO.getOrderId());
        }

        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery.setOrder(order);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return deliveryMapper.toDTO(savedDelivery);
    }

    @Override
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO deliveryDTO) {
        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", id));

        Order order = orderRepository.findById(deliveryDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", deliveryDTO.getOrderId()));

        deliveryMapper.updateEntityFromDTO(deliveryDTO, existingDelivery);
        existingDelivery.setOrder(order);

        Delivery updatedDelivery = deliveryRepository.save(existingDelivery);

        return deliveryMapper.toDTO(updatedDelivery);
    }

    @Override
    public void deleteDelivery(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery", "id", id);
        }

        deliveryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryDTO> getAllDeliveries(Pageable pageable) {
        Page<Delivery> deliveries = deliveryRepository.findAll(pageable);
        return deliveries.map(deliveryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", id));
        return deliveryMapper.toDTO(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrder_IdOrder(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order ID: " + orderId));
        return deliveryMapper.toDTO(delivery);
    }
}