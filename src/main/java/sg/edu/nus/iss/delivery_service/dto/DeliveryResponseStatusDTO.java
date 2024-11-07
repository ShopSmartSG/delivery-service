package sg.edu.nus.iss.delivery_service.dto;

import lombok.Data;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;

import java.util.UUID;

@Data
public class DeliveryResponseStatusDTO {
    private UUID orderId;
    private DeliveryStatus status;
    private UUID deliveryPersonId;
    private UUID customerId;
    private String message;

    public DeliveryResponseStatusDTO(UUID orderId, DeliveryStatus status, UUID deliveryPersonId, UUID customerId, String message) {
        this.orderId = orderId;
        this.status = status;
        this.deliveryPersonId = deliveryPersonId;
        this.customerId = customerId;
        this.message = message;
    }
}
