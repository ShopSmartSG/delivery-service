package sg.edu.nus.iss.delivery_service.model;

import jakarta.persistence.Id;
import lombok.Data;

import jakarta.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name="deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID orderId;
    private UUID deliveryPersonId;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private UUID customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

