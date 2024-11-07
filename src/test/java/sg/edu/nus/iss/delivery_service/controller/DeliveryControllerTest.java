package sg.edu.nus.iss.delivery_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseStatusDTO;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;
import sg.edu.nus.iss.delivery_service.service.DeliveryService;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeliveryControllerTest {

    @InjectMocks
    private DeliveryController deliveryController;

    @Mock
    private DeliveryService deliveryService;

    private UUID orderId;
    private UUID deliveryPersonId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderId = randomUUID();
        deliveryPersonId = randomUUID();
        customerId = randomUUID();
    }

    @Test
    void createDeliveryStatus_shouldReturnCreatedResponse() {
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PENDING_PICKUP, deliveryPersonId, customerId, "Delivery status created");
        when(deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId)).thenReturn(new ResponseEntity<>(responseDto, HttpStatus.OK));

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status created", response.getBody().getMessage());
        verify(deliveryService, times(1)).createDeliveryStatus(orderId, deliveryPersonId, customerId);
    }

    @Test
    void updateDeliveryStatus_shouldReturnUpdatedResponse() {
        DeliveryResponseStatusDTO updateDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "Delivery status updated");
        when(deliveryService.updateDeliveryStatus(updateDto)).thenReturn(new ResponseEntity<>(responseDto, HttpStatus.OK));

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.updateDeliveryStatus(updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status updated", response.getBody().getMessage());
        verify(deliveryService, times(1)).updateDeliveryStatus(updateDto);
    }
}
