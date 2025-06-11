package software.sebastian.oposiciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import software.sebastian.oposiciones.service.ConvocatoriaGeneratorService;

@SpringBootApplication
@Profile("generate-convocatorias")
public class ConvocatoriaGeneratorRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ConvocatoriaGeneratorRunner.class);
    
    private final ConvocatoriaGeneratorService convocatoriaGeneratorService;
    private final ApplicationContext context;

    public ConvocatoriaGeneratorRunner(ConvocatoriaGeneratorService convocatoriaGeneratorService,
                                      ApplicationContext context) {
        this.convocatoriaGeneratorService = convocatoriaGeneratorService;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting manual convocatoria generation...");
        try {
            convocatoriaGeneratorService.generateConvocatorias();
            logger.info("Convocatoria generation completed successfully");
        } catch (Exception e) {
            logger.error("Error during convocatoria generation", e);
            System.exit(1);
        }
        
        // Exit after completion
        System.exit(SpringApplication.exit(context, () -> 0));
    }
}