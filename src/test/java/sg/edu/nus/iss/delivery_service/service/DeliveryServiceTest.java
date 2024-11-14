package sg.edu.nus.iss.delivery_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

        DeliveryResponseStatusDTO response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals("Delivery status created", response.getMessage());
        assertEquals(DeliveryStatus.DELIVERY_ACCEPTED, response.getStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_ExistingDelivery() {
        Delivery existingDelivery = new Delivery();
        existingDelivery.setOrderId(orderId);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));

        DeliveryResponseStatusDTO response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, customerId);

        assertEquals("Delivery status already exists", response.getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_InvalidInput_NullOrderId() {
        DeliveryResponseStatusDTO response = deliveryService.createDeliveryStatus(null, deliveryPersonId, customerId);

        assertEquals("Invalid input: fields must not be null", response.getMessage());
        assertNull(response.getStatus());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_InvalidInput_NullDeliveryPersonId() {
        DeliveryResponseStatusDTO response = deliveryService.createDeliveryStatus(orderId, null, customerId);

        assertEquals("Invalid input: fields must not be null", response.getMessage());
        assertNull(response.getStatus());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testCreateDeliveryStatus_InvalidInput_NullCustomerId() {
        DeliveryResponseStatusDTO response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId, null);

        assertEquals("Invalid input: fields must not be null", response.getMessage());
        assertNull(response.getStatus());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_DeliveryFound_ValidTransition() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERY_ACCEPTED);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals("Delivery status updated", response.getMessage());
        assertEquals(DeliveryStatus.DELIVERY_PICKED_UP, response.getStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_DeliveryNotFound() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals("Delivery not found", response.getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_InvalidStatusTransition() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERY_PICKED_UP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_ACCEPTED, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals("Invalid status transition", response.getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_SameStatus() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERY_PICKED_UP);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals("Invalid status transition", response.getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testUpdateDeliveryStatus_FromCompletedToAny() {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(DeliveryStatus.COMPLETED);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.CANCELLED, deliveryPersonId, customerId, null);
        DeliveryResponseStatusDTO response = deliveryService.updateDeliveryStatus(updateDTO);

        assertEquals("Invalid status transition", response.getMessage());

        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void testIsValidStatusTransition_FromDeliveryAcceptedToPickedUp() {
        assertTrue(deliveryService.isValidStatusTransition(DeliveryStatus.DELIVERY_ACCEPTED, DeliveryStatus.DELIVERY_PICKED_UP));
    }

    @Test
    void testIsValidStatusTransition_FromPickedUpToCompleted() {
        assertTrue(deliveryService.isValidStatusTransition(DeliveryStatus.DELIVERY_PICKED_UP, DeliveryStatus.COMPLETED));
    }

    @Test
    void testIsValidStatusTransition_FromCompletedToCancelled() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.COMPLETED, DeliveryStatus.CANCELLED));
    }

    @Test
    void testIsValidStatusTransition_InvalidNullStatusTransition() {
        assertFalse(deliveryService.isValidStatusTransition(DeliveryStatus.DELIVERY_ACCEPTED, null));
    }

    @Test
    void testUpdateDeliveryStatus_NullStatusUpdateDTO() {
        assertThrows(NullPointerException.class, () ->
                deliveryService.updateDeliveryStatus(null));
    }

    @Test
    void testUpdateDeliveryStatus_RepositoryException() {
        when(deliveryRepository.findByOrderId(orderId)).thenThrow(new RuntimeException("Database error"));

        DeliveryResponseStatusDTO updateDTO = new DeliveryResponseStatusDTO(orderId, DeliveryStatus.DELIVERY_PICKED_UP, deliveryPersonId, customerId, null);
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                deliveryService.updateDeliveryStatus(updateDTO));

        assertEquals("Database error", thrown.getMessage());
    }
}
