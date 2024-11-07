package sg.edu.nus.iss.delivery_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.edu.nus.iss.delivery_service.model.Delivery;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    Optional<Delivery> findByOrderId(String orderId);
}
