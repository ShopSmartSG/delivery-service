package sg.edu.nus.iss.delivery_service.config;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import sg.edu.nus.iss.delivery_service.repository.DeliveryRepository;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "zapscan")
public class MockRepositoryConfig {

    @Bean
    @Primary
    public DeliveryRepository merchantRepository() {
        return Mockito.mock(DeliveryRepository.class);
    }
} 