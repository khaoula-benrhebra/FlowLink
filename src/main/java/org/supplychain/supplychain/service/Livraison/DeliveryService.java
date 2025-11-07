package org.supplychain.supplychain.service.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Livraison.DeliveryDTO;

public interface DeliveryService {

    DeliveryDTO createDelivery(DeliveryDTO deliveryDTO);

    DeliveryDTO updateDelivery(Long id, DeliveryDTO deliveryDTO);

    void deleteDelivery(Long id);

    Page<DeliveryDTO> getAllDeliveries(Pageable pageable);

    DeliveryDTO getDeliveryById(Long id);

    DeliveryDTO getDeliveryByOrderId(Long orderId);
}