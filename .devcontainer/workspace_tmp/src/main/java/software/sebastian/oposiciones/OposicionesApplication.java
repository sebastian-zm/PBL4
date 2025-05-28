package software.sebastian.oposiciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OposicionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(OposicionesApplication.class, args);
	}

	
}
