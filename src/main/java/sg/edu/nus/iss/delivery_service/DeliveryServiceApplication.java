package sg.edu.nus.iss.delivery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DeliveryServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(DeliveryServiceApplication.class, args);
	}
}
