package software.sebastian.oposiciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import software.sebastian.oposiciones.model.Modelo;

/**
 * Repositorio para la entidad MODELO.
 */
@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Integer> {

    /**
     * Busca un modelo por su nombre.
     * 
     * @param nombre Nombre único del modelo.
     * @return Optional con el Modelo si existe.
     */
    Optional<Modelo> findByNombre(String nombre);
}
