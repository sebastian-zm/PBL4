package software.sebastian.oposiciones.service;

import java.util.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public CustomUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Mapear permisos a roles
        List<GrantedAuthority> roles = new ArrayList<>();
        if ((u.getPermisos() & 1) != 0) { roles.add(new SimpleGrantedAuthority("ROLE_USER")); }
        if ((u.getPermisos() & 2) != 0) { roles.add(new SimpleGrantedAuthority("ROLE_ADMIN")); }

        return new User(u.getEmail(), u.getPasswordHash(), roles);
    }
}
