package software.sebastian.oposiciones.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import software.sebastian.oposiciones.service.TokenizerPythonCaller;

@Configuration
public class AppConfig {

    @Bean
    public TokenizerPythonCaller tokenizerPythonCaller() {
        return new TokenizerPythonCaller();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "taggingExecutor")
    public TaskExecutor taggingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Tagging-");
        executor.initialize();
        return executor;
    }

}
