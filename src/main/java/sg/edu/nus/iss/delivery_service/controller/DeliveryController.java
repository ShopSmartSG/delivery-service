package sg.edu.nus.iss.delivery_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseStatusDTO;
import sg.edu.nus.iss.delivery_service.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("")
    public ResponseEntity<DeliveryResponseStatusDTO> createDeliveryStatus(
            @RequestParam UUID orderId,
            @RequestParam UUID deliveryPersonId,
            @RequestParam UUID customerId) {
        return deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);
    }

    @PutMapping("/status")
    public ResponseEntity<DeliveryResponseStatusDTO> updateDeliveryStatus(
            @RequestBody DeliveryResponseStatusDTO statusUpdateDTO) {
        return deliveryService.updateDeliveryStatus(statusUpdateDTO);
    }
}
