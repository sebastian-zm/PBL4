package software.sebastian.oposiciones.config;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import software.sebastian.oposiciones.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(userDetailsService);
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) qué rutas son públicas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/", "/convocatorias", "/etiquetas" ,"/registro", "/css/**", "/js/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/registro")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/etiquetas/**").hasRole("ADMIN")
                        // el resto de URLs (p.ej. /suscripciones, /) autenticado (USER o ADMIN)
                        .anyRequest().authenticated())
                // 3) login/logout
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")        // al cerrar sesión, redirige a la página de inicio
                    .invalidateHttpSession(true)  // invalida la sesión HTTP
                    .clearAuthentication(true)    // limpia el objeto de autenticación
                    .deleteCookies("JSESSIONID")  // borra la cookie de sesión
                    )
                // 4) CSRF (por defecto ON); vamos a usar cookie repo para poder leerlo en JS/fetch
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
