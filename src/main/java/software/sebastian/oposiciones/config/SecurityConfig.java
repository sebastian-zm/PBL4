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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import software.sebastian.oposiciones.service.CustomUserDetailsService;
import software.sebastian.oposiciones.config.CustomLogoutSuccessHandler;

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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/", "/convocatorias", "/etiquetas", "/registro", "/css/**", "/js/**", "/webjars/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")
                .requestMatchers("/chat/**", "/topic/**", "/app/**", "/ws/**").permitAll()
                .requestMatchers("/foro/**").hasAnyRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
            .logoutUrl("/logout")                      // POST /logout
            .logoutSuccessHandler(new CustomLogoutSuccessHandler())
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            )

            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/notifications/**")
            );

        return http.build();
    }
}
