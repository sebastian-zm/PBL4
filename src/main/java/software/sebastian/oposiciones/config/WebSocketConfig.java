// WebSocketConfig.java
package software.sebastian.oposiciones.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS(); // foro
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();   // notificaciones
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // "/topic" para mensajes que van a los usuarios suscritos (broadcast)
    // "/queue" para mensajes individuales (notificaciones privadas si se quiere)
    registry.enableSimpleBroker("/topic", "/queue");

    // Lo que envían los clientes debe empezar con /app
    registry.setApplicationDestinationPrefixes("/app");
  }
}
