package software.sebastian.oposiciones.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.repository.UsuarioRepository;
import software.sebastian.oposiciones.model.Usuario;

import java.security.Principal;
import java.util.List;

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
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    @Transactional
    public Usuario create(String nombre, String apodo, String email, String password, Integer permisos) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (userRepo.findByApodo(apodo).isPresent()) {
            throw new IllegalArgumentException("El apodo ya está en uso");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordHash = passwordEncoder.encode(password);

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApodo(apodo);
        u.setEmail(email);
        u.setPermisos(permisos);
        u.setPasswordHash(passwordHash);
        return userRepo.save(u);
    }
}
