package sg.edu.nus.iss.delivery_service.dto;

import lombok.Data;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;

@Data
public class DeliveryResponseDTO {
    private String id;
    private DeliveryStatus status;
    private String deliveryPersonId;
    private String message;

    public DeliveryResponseDTO(String id, DeliveryStatus status, String deliveryPersonId, String message) {
        this.id = id;
        this.status = status;
        this.deliveryPersonId = deliveryPersonId;
        this.message = message;
    }
}
