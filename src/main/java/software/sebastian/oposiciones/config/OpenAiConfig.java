// src/main/java/software/sebastian/oposiciones/config/OpenAiConfig.java
package software.sebastian.oposiciones.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el cliente oficial de OpenAI para inyección en Spring.
 */
@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key}")
    private String apiKey;

    /**
     * Crea un bean de OpenAIClient usando las variables de entorno:
     * OPENAI_API_KEY (requerida), OPENAI_ORG_ID y OPENAI_PROJECT_ID (opcionales).
     */
    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.builder()
        .apiKey(apiKey)
        // .organization("YOUR_ORG_ID")    // optional
        // .project("YOUR_PROJECT_ID")     // optional
        // .baseUrl("https://api.openai.com/v1") // optional if you need a custom endpoint
        .build();
    }
}
