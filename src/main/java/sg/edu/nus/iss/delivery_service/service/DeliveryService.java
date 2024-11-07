package sg.edu.nus.iss.delivery_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseDTO;
import sg.edu.nus.iss.delivery_service.dto.DeliveryStatusUpdateDTO;
import sg.edu.nus.iss.delivery_service.model.Delivery;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;
import sg.edu.nus.iss.delivery_service.repository.DeliveryRepository;

import java.util.Optional;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // Method to create a new delivery status for an order
    public ResponseEntity<DeliveryResponseDTO> createDeliveryStatus(String orderId, String deliveryPersonId) {
        Optional<Delivery> existingDelivery = deliveryRepository.findByOrderId(orderId);

        if (existingDelivery.isPresent()) {
            log.warn("Delivery status for order ID {} already exists", orderId);
            return new ResponseEntity<>(new DeliveryResponseDTO(orderId, null, deliveryPersonId, "Delivery status already exists"), HttpStatus.CONFLICT);
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setDeliveryPersonId(deliveryPersonId);
        delivery.setStatus(DeliveryStatus.PENDING_PICKUP);  // Initialize with PENDING_PICKUP status

        deliveryRepository.save(delivery);

        log.info("Created new delivery status for order ID {}", orderId);
        return new ResponseEntity<>(new DeliveryResponseDTO(orderId, DeliveryStatus.PENDING_PICKUP, deliveryPersonId, "Delivery status created"), HttpStatus.OK);
    }

    // Method to update delivery status based on the current stage
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(DeliveryStatusUpdateDTO statusUpdateDTO) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findByOrderId(statusUpdateDTO.getOrderId());

        if (deliveryOptional.isEmpty()) {
            log.warn("No delivery found for order ID {}", statusUpdateDTO.getOrderId());
            return new ResponseEntity<>(new DeliveryResponseDTO(statusUpdateDTO.getOrderId(), null, statusUpdateDTO.getDeliveryPersonId(), "Delivery not found"), HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryOptional.get();

        // Ensure the new status is a valid next stage
        if (!isValidStatusTransition(delivery.getStatus(), statusUpdateDTO.getStatus())) {
            log.warn("Invalid status transition for order ID {} from {} to {}", statusUpdateDTO.getOrderId(), delivery.getStatus(), statusUpdateDTO.getStatus());
            return new ResponseEntity<>(new DeliveryResponseDTO(statusUpdateDTO.getOrderId(), delivery.getStatus(), statusUpdateDTO.getDeliveryPersonId(), "Invalid status transition"), HttpStatus.BAD_REQUEST);
        }

        // Update the status and save changes
        delivery.setStatus(statusUpdateDTO.getStatus());
        delivery.setDeliveryPersonId(statusUpdateDTO.getDeliveryPersonId());
        deliveryRepository.save(delivery);

        log.info("Updated delivery status for order ID {} to {}", statusUpdateDTO.getOrderId(), statusUpdateDTO.getStatus());
        return new ResponseEntity<>(new DeliveryResponseDTO(statusUpdateDTO.getOrderId(), statusUpdateDTO.getStatus(), statusUpdateDTO.getDeliveryPersonId(), "Delivery status updated"), HttpStatus.OK);
    }

    // Helper method to validate status transitions
    private boolean isValidStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        return switch (currentStatus) {
            case PENDING_PICKUP -> newStatus == DeliveryStatus.PICKED_UP || newStatus == DeliveryStatus.CANCELLED;
            case PICKED_UP -> newStatus == DeliveryStatus.DELIVERED || newStatus == DeliveryStatus.CANCELLED;
            case DELIVERED, CANCELLED -> false;  // No transitions allowed from these states
        };
    }
}
