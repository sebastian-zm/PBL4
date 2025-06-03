// WebSocketConfig.java
package software.sebastian.oposiciones.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS(); // foro
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();   // notificaciones
  }

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
    // "/topic" para mensajes que van a los usuarios suscritos (broadcast)
    // "/queue" para mensajes individuales (notificaciones privadas si se quiere)
    registry.enableSimpleBroker("/topic", "/queue");

    // Lo que envían los clientes debe empezar con /app
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");

  }
}
