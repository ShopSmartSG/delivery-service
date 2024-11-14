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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        orderId = UUID.randomUUID();
        deliveryPersonId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    @Test
    void createDeliveryStatus_shouldReturnCreatedResponse() {
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_ACCEPTED, deliveryPersonId, customerId, "Delivery status created");
        when(deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId)).thenReturn(responseDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.createDeliveryStatus(responseDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status created", response.getBody().getMessage());
        verify(deliveryService, times(1)).createDeliveryStatus(orderId, deliveryPersonId, customerId);
    }

    @Test
    void createDeliveryStatus_shouldReturnBadRequestResponse() {
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_ACCEPTED, deliveryPersonId, customerId, "Invalid input data");
        when(deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId)).thenReturn(responseDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.createDeliveryStatus(responseDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(deliveryService, times(1)).createDeliveryStatus(orderId, deliveryPersonId, customerId);
    }

    @Test
    void createDeliveryStatus_shouldReturnConflictResponse() {
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_ACCEPTED, deliveryPersonId, customerId, "Delivery already exists");
        when(deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId)).thenReturn(responseDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.createDeliveryStatus(responseDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(deliveryService, times(1)).createDeliveryStatus(orderId, deliveryPersonId, customerId);
    }

    @Test
    void updateDeliveryStatus_shouldReturnUpdatedResponse() {
        DeliveryResponseStatusDTO updateDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO responseDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, "Delivery status updated");
        when(deliveryService.updateDeliveryStatus(updateDto)).thenReturn(responseDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.updateDeliveryStatus(updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status updated", response.getBody().getMessage());
        verify(deliveryService, times(1)).updateDeliveryStatus(updateDto);
    }

    @Test
    void updateDeliveryStatus_shouldReturnNotFoundResponse() {
        DeliveryResponseStatusDTO updateDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, "Delivery not found");
        when(deliveryService.updateDeliveryStatus(updateDto)).thenReturn(updateDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.updateDeliveryStatus(updateDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(deliveryService, times(1)).updateDeliveryStatus(updateDto);
    }

    @Test
    void updateDeliveryStatus_shouldReturnBadRequestResponse() {
        DeliveryResponseStatusDTO updateDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, "Invalid status transition");
        when(deliveryService.updateDeliveryStatus(updateDto)).thenReturn(updateDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryController.updateDeliveryStatus(updateDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(deliveryService, times(1)).updateDeliveryStatus(updateDto);
    }

    @Test
    void updateDeliveryStatus_shouldHandleServiceException() {
        DeliveryResponseStatusDTO updateDto = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);

        // Mock the service method to throw a RuntimeException when called with the updateDto
        doThrow(new RuntimeException("Update failed")).when(deliveryService).updateDeliveryStatus(updateDto);

        ResponseEntity<DeliveryResponseStatusDTO> response = null;
        try {
            response = deliveryController.updateDeliveryStatus(updateDto);
        } catch (RuntimeException e) {
            assertEquals("Update failed", e.getMessage()); // Ensure exception message matches
        }

        // Ensure that the service method was called once despite the exception
        verify(deliveryService, times(1)).updateDeliveryStatus(updateDto);

        // Verify response entity as null since an exception would stop the response
        assertNull(response);
    }
}
