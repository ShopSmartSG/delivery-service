package sg.edu.nus.iss.delivery_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseDTO;
import sg.edu.nus.iss.delivery_service.dto.DeliveryStatusUpdateDTO;
import sg.edu.nus.iss.delivery_service.service.DeliveryService;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/create")
    public ResponseEntity<DeliveryResponseDTO> createDeliveryStatus(
            @RequestParam String orderId,
            @RequestParam String deliveryPersonId) {
        return deliveryService.createDeliveryStatus(orderId, deliveryPersonId);
    }

    @PutMapping("/update-status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @RequestBody DeliveryStatusUpdateDTO statusUpdateDTO) {
        return deliveryService.updateDeliveryStatus(statusUpdateDTO);
    }
}

