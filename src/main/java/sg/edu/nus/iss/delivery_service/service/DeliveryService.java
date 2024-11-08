package sg.edu.nus.iss.delivery_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseStatusDTO;
import sg.edu.nus.iss.delivery_service.model.Delivery;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;
import sg.edu.nus.iss.delivery_service.repository.DeliveryRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public ResponseEntity<DeliveryResponseStatusDTO> createDeliveryStatus(UUID orderId, UUID deliveryPersonId, UUID customerId) {
        if (orderId == null || deliveryPersonId == null || customerId == null) {
            log.error("Invalid input: orderId, deliveryPersonId, and customerId must not be null");
            return new ResponseEntity<>(new DeliveryResponseStatusDTO(null, null, null, null, "Invalid input: fields must not be null"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<Delivery> existingDelivery = deliveryRepository.findByOrderId(orderId);

        if (existingDelivery.isPresent()) {
            log.warn("Delivery status for order ID {} already exists", orderId);
            return new ResponseEntity<>(new DeliveryResponseStatusDTO(orderId, null, deliveryPersonId, customerId, "Delivery status already exists"), HttpStatus.CONFLICT);
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setDeliveryPersonId(deliveryPersonId);
        delivery.setCustomerId(customerId);
        delivery.setStatus(DeliveryStatus.DELIVERY_ACCEPTED);

        deliveryRepository.save(delivery);

        log.info("Created new delivery status for order ID {}", orderId);
        return new ResponseEntity<>(new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_ACCEPTED, deliveryPersonId, customerId, "Delivery status created"), HttpStatus.OK);
    }


    public ResponseEntity<DeliveryResponseStatusDTO> updateDeliveryStatus(DeliveryResponseStatusDTO statusUpdateDTO) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findByOrderId(statusUpdateDTO.getOrderId());

        if (deliveryOptional.isEmpty()) {
            log.warn("No delivery found for order ID {}", statusUpdateDTO.getOrderId());
            return new ResponseEntity<>(new DeliveryResponseStatusDTO(statusUpdateDTO.getOrderId(), null, statusUpdateDTO.getDeliveryPersonId(), statusUpdateDTO.getCustomerId(), "Delivery not found"), HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryOptional.get();

        if (!isValidStatusTransition(delivery.getStatus(), statusUpdateDTO.getStatus())) {
            log.warn("Invalid status transition for order ID {} from {} to {}", statusUpdateDTO.getOrderId(), delivery.getStatus(), statusUpdateDTO.getStatus());
            return new ResponseEntity<>(new DeliveryResponseStatusDTO(statusUpdateDTO.getOrderId(), delivery.getStatus(), statusUpdateDTO.getDeliveryPersonId(), statusUpdateDTO.getCustomerId(), "Invalid status transition"), HttpStatus.BAD_REQUEST);
        }

        delivery.setStatus(statusUpdateDTO.getStatus());
        delivery.setDeliveryPersonId(statusUpdateDTO.getDeliveryPersonId());
        deliveryRepository.save(delivery);

        log.info("Updated delivery status for order ID {} to {}", statusUpdateDTO.getOrderId(), statusUpdateDTO.getStatus());
        return new ResponseEntity<>(new DeliveryResponseStatusDTO(statusUpdateDTO.getOrderId(), statusUpdateDTO.getStatus(), statusUpdateDTO.getDeliveryPersonId(), statusUpdateDTO.getCustomerId(), "Delivery status updated"), HttpStatus.OK);
    }

    boolean isValidStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        return switch (currentStatus) {
            case DELIVERY_ACCEPTED -> newStatus == DeliveryStatus.DELIVERY_PICKED_UP || newStatus == DeliveryStatus.CANCELLED;
            case DELIVERY_PICKED_UP -> newStatus == DeliveryStatus.COMPLETED || newStatus == DeliveryStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
