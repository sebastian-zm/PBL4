package software.sebastian.oposiciones.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.repository.UsuarioRepository;
import software.sebastian.oposiciones.model.Usuario;


@Service
public class UsuarioService {
    private final UsuarioRepository userRepo;

    public UsuarioService(UsuarioRepository ur) {
        this.userRepo = ur;
    }

    public List<Usuario> findAll() {
        return userRepo.findAll();
    }


    public Usuario getCurrentUser(Principal principal) {
        String username = principal.getName();
        Usuario usuario = userRepo.findByEmail(username).orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        return usuario;
    }

    @Transactional
    public Usuario create(String nombre, String email, String password, Integer permisos) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordHash = passwordEncoder.encode(password);

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPermisos(permisos);
        u.setPasswordHash(passwordHash);
        Usuario uSaved = userRepo.save(u);
        return uSaved;
    }
}
