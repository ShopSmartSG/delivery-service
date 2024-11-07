//package sg.edu.nus.iss.delivery_service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import sg.edu.nus.iss.delivery_service.dto.DeliveryResponseDTO;
//import sg.edu.nus.iss.delivery_service.dto.DeliveryStatusUpdateDTO;
//import sg.edu.nus.iss.delivery_service.model.Delivery;
//import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;
//import sg.edu.nus.iss.delivery_service.repository.DeliveryRepository;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class DeliveryServiceTest {
//
//    @InjectMocks
//    private DeliveryService deliveryService;
//
//    @Mock
//    private DeliveryRepository deliveryRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createDeliveryStatus_shouldReturnConflictWhenDeliveryExists() {
//        String orderId = "order123";
//        String deliveryPersonId = "person123";
//        UUID customerId = UUID.randomUUID();
//
//        Delivery existingDelivery = new Delivery();
//        existingDelivery.setId(UUID.randomUUID().toString());
//        existingDelivery.setOrderId(orderId);
//        existingDelivery.setStatus(DeliveryStatus.PENDING_PICKUP);
//        existingDelivery.setDeliveryPersonId(deliveryPersonId);
//        existingDelivery.setCreatedAt(LocalDateTime.now());
//        existingDelivery.setUpdatedAt(LocalDateTime.now());
//        existingDelivery.setCustomerId(customerId);
//
//        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));
//
//        ResponseEntity<DeliveryResponseDTO> response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("Delivery status already exists", response.getBody().getMessage());
//        verify(deliveryRepository, never()).save(any(Delivery.class));
//    }
//
//    @Test
//    void createDeliveryStatus_shouldCreateNewDeliveryStatus() {
//        String orderId = "order123";
//        String deliveryPersonId = "person123";
//        UUID customerId = UUID.randomUUID();
//
//        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
//
//        ResponseEntity<DeliveryResponseDTO> response = deliveryService.createDeliveryStatus(orderId, deliveryPersonId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Delivery status created", response.getBody().getMessage());
//        assertEquals(DeliveryStatus.PENDING_PICKUP, response.getBody().getStatus());
//        verify(deliveryRepository, times(1)).save(any(Delivery.class));
//    }
//
//    @Test
//    void updateDeliveryStatus_shouldReturnNotFoundWhenDeliveryDoesNotExist() {
//        String orderId = "order123";
//        DeliveryStatusUpdateDTO statusUpdateDTO = new DeliveryStatusUpdateDTO(orderId, DeliveryStatus.DELIVERED, "person123");
//        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
//
//        ResponseEntity<DeliveryResponseDTO> response = deliveryService.updateDeliveryStatus(statusUpdateDTO);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Delivery not found", response.getBody().getMessage());
//        verify(deliveryRepository, never()).save(any(Delivery.class));
//    }
//
//    @Test
//    void updateDeliveryStatus_shouldReturnBadRequestForInvalidTransition() {
//        String orderId = "order123";
//        DeliveryStatusUpdateDTO statusUpdateDTO = new DeliveryStatusUpdateDTO(orderId, DeliveryStatus.DELIVERED, "person123");
//        Delivery existingDelivery = new Delivery();
//        existingDelivery.setId(UUID.randomUUID().toString());
//        existingDelivery.setOrderId(orderId);
//        existingDelivery.setStatus(DeliveryStatus.PENDING_PICKUP);
//        existingDelivery.setDeliveryPersonId("person123");
//        existingDelivery.setCreatedAt(LocalDateTime.now());
//        existingDelivery.setUpdatedAt(LocalDateTime.now());
//        existingDelivery.setCustomerId(UUID.randomUUID());
//
//        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));
//
//        ResponseEntity<DeliveryResponseDTO> response = deliveryService.updateDeliveryStatus(statusUpdateDTO);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Invalid status transition", response.getBody().getMessage());
//        verify(deliveryRepository, never()).save(any(Delivery.class));
//    }
//
//    @Test
//    void updateDeliveryStatus_shouldUpdateStatusSuccessfully() {
//        String orderId = "order123";
//        DeliveryStatusUpdateDTO statusUpdateDTO = new DeliveryStatusUpdateDTO(orderId, DeliveryStatus.PICKED_UP, "person123");
//        Delivery existingDelivery = new Delivery();
//        existingDelivery.setId(UUID.randomUUID().toString());
//        existingDelivery.setOrderId(orderId);
//        existingDelivery.setStatus(DeliveryStatus.PENDING_PICKUP);
//        existingDelivery.setDeliveryPersonId("person123");
//        existingDelivery.setCreatedAt(LocalDateTime.now());
//        existingDelivery.setUpdatedAt(LocalDateTime.now());
//        existingDelivery.setCustomerId(UUID.randomUUID());
//
//        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingDelivery));
//        when(deliveryRepository.save(any(Delivery.class))).thenReturn(existingDelivery);
//
//        ResponseEntity<DeliveryResponseDTO> response = deliveryService.updateDeliveryStatus(statusUpdateDTO);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Delivery status updated", response.getBody().getMessage());
//        assertEquals(DeliveryStatus.PICKED_UP, response.getBody().getStatus());
//        verify(deliveryRepository, times(1)).save(existingDelivery);
//    }
//}
