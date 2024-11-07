package sg.edu.nus.iss.delivery_service.dto;

import lombok.Data;
import sg.edu.nus.iss.delivery_service.model.DeliveryStatus;

@Data
public class DeliveryStatusUpdateDTO {
    private String orderId;
    private DeliveryStatus status;
    private String deliveryPersonId;
}
