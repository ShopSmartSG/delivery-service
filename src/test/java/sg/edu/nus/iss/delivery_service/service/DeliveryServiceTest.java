package sg.edu.nus.iss.delivery_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseStatusDTO;
import sg.edu.nus.iss.delivery_service.model.Delivery;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;
import sg.edu.nus.iss.delivery_service.repository.DeliveryRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
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
    void testCreateDeliveryStatus_NewDelivery() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status created", response.getBody().getMessage());
        assertEquals(DeliveryStatus.PENDING_PICKUP, response.getBody().getStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_ExistingDelivery() {
        Delivery existingDelivery = new Delivery();
        existingDelivery.setOrderId(orderId);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Delivery status already exists", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_DeliveryFound_ValidTransition() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.PENDING_PICKUP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status updated", response.getBody().getMessage());
        assertEquals(DeliveryStatus.PICKED_UP, response.getBody().getStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_DeliveryNotFound() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_InvalidStatusTransition() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PENDING_PICKUP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status transition", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_SameStatus() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status transition", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_FromDeliveredToAny() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.CANCELLED, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status transition", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_FromCancelledToAny() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.CANCELLED);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status transition", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_WithNullFields() {
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.createDeliveryStatus(null, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Invalid input: fields must not be null", response.getBody().getMessage());
    }

    @Test
    void testIsValidStatusTransition_FromPendingToInvalid() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.PENDING_PICKUP, DeliveryStatus.DELIVERED));
    }

    @Test
    void testIsValidStatusTransition_FromPickedUpToPickedUp() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.PICKED_UP, DeliveryStatus.PICKED_UP));
    }

    @Test
    void testIsValidStatusTransition_FromDeliveredToCancelled() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED));
    }

    @Test
    void testUpdateDeliveryStatus_WithSameDeliveryPersonId() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.PENDING_PICKUP);
        delivery.setDeliveryPersonId(deliveryPersonId);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status updated", response.getBody().getMessage());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_WithExistingOrder() {
        Delivery existingDelivery = new Delivery();
        existingDelivery.setOrderId(orderId);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Delivery status already exists", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_WithNullOrderId() {
        DeliveryResponseStatusDTO statusUpdateDTO = new DeliveryResponseStatusDTO(null, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");

        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(statusUpdateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found", response.getBody().getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_WithValidTransition() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.PENDING_PICKUP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO statusUpdateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.PICKED_UP, deliveryPersonId, customerId, "");
        ResponseEntity<DeliveryResponseStatusDTO> response = deliveryService.updateDeliveryStatus(statusUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery status updated", response.getBody().getMessage());
        assertEquals(DeliveryStatus.PICKED_UP, response.getBody().getStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testIsValidStatusTransition_FromPickedUpToCancelled() {
        assertTrue(deliveryService.isValidStatusTransition(DeliveryStatus.PICKED_UP, DeliveryStatus.CANCELLED));
    }

    @Test
    void testIsValidStatusTransition_FromDeliveredToDelivered() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.DELIVERED, DeliveryStatus.DELIVERED));
    }
}
