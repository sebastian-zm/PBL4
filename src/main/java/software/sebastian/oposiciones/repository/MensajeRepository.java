package software.sebastian.oposiciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Mensaje;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    //Esto ya le dice a Spring: 1. Buscar en la entidad Mensaje 2. Filtrar por mensaje.hilo.hiloId 3. Ordenar por createdAt ascendente
    // Spring genera la consulta automáticamente.
    
    List<Mensaje> findByHilo_HiloIdOrderByCreatedAtAsc(Integer hiloId);
}