package sg.edu.nus.iss.delivery_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseStatusDTO;
import sg.edu.nus.iss.delivery_service.service.DeliveryService;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/")
    public ResponseEntity<DeliveryResponseStatusDTO> createDeliveryStatus(
            @RequestBody DeliveryResponseStatusDTO requestDTO) {
        DeliveryResponseStatusDTO responseDTO = deliveryService.createDeliveryStatus(
                requestDTO.getOrderId(), requestDTO.getDeliveryPersonId(), requestDTO.getCustomerId()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/status")
    public ResponseEntity<DeliveryResponseStatusDTO> updateDeliveryStatus(
            @RequestBody DeliveryResponseStatusDTO statusUpdateDTO) {
        DeliveryResponseStatusDTO responseDTO = deliveryService.updateDeliveryStatus(statusUpdateDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
