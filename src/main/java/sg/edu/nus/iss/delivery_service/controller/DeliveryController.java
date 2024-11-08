package sg.edu.nus.iss.delivery_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        // Call the service to create delivery status and get the DTO
        DeliveryResponseStatusDTO responseDTO = deliveryService.createDeliveryStatus(
                requestDTO.getOrderId(), requestDTO.getDeliveryPersonId(), requestDTO.getCustomerId());

        // Return appropriate ResponseEntity based on the response from service
        if (responseDTO.getMessage().contains("Invalid input")) {
            return ResponseEntity.badRequest().body(responseDTO); // 400 Bad Request
        }
        if (responseDTO.getMessage().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO); // 409 Conflict if delivery status exists
        }
        if (responseDTO.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO); // 500 Internal Server Error
        }
        return ResponseEntity.ok(responseDTO); // 200 OK
    }

    @PutMapping("/status")
    public ResponseEntity<DeliveryResponseStatusDTO> updateDeliveryStatus(
            @RequestBody DeliveryResponseStatusDTO statusUpdateDTO) {

        // Call the service to update the delivery status and get the DTO
        DeliveryResponseStatusDTO responseDTO = deliveryService.updateDeliveryStatus(statusUpdateDTO);

        // Return appropriate ResponseEntity based on the response from service
        if (responseDTO.getMessage().contains("Delivery not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO); // 404 Not Found if no delivery found
        }
        if (responseDTO.getMessage().contains("Invalid status transition")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO); // 400 Bad Request if invalid status transition
        }
        if (responseDTO.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO); // 500 Internal Server Error
        }
        return ResponseEntity.ok(responseDTO); // 200 OK
    }
}
