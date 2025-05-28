package software.sebastian.oposiciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.sebastian.oposiciones.service.TokenizerPythonCaller;

@Configuration
public class AppConfig {

    @Bean
    public TokenizerPythonCaller tokenizerPythonCaller() {
        return new TokenizerPythonCaller();
    }

}
