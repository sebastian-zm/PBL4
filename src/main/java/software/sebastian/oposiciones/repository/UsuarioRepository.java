package software.sebastian.oposiciones.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
