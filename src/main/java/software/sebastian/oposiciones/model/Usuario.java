package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "USUARIO")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    /**
     * Ejemplo de bitmask:
     *   1 → ROLE_USER
     *   2 → ROLE_ADMIN
     */
    @Column(nullable = false)
    private Integer permisos;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters y Setters
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getPermisos() { return permisos; }
    public void setPermisos(Integer permisos) { this.permisos = permisos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ------------------- Implementación de UserDetails -------------------

    /**
     * Implementamos UserDetails para que Spring Security pueda usar
     * directamente esta clase como principal en el Authentication,
     * evitando problemas de casteo y facilitando el acceso al usuario 
     * autenticado con toda su información.
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Mapear permisos a roles - ejemplo simple
        if ((permisos & 1) == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        if ((permisos & 2) == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;  // usamos el email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // podrías implementar lógica real si quieres
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // idem
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // idem
    }

    @Override
    public boolean isEnabled() {
        return true; // idem
    }
}