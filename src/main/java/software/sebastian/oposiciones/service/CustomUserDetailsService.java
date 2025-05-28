package software.sebastian.oposiciones.service;

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

        // Ya que Usuario implementa UserDetails y getAuthorities está implementado ahí,
        // simplemente devolvemos el objeto Usuario directamente
        return u;
    }
}
