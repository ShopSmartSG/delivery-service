package sg.edu.nus.iss.delivery_service.model;

import jakarta.persistence.Table;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String orderId;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private String deliveryPersonId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID customerId;
}

