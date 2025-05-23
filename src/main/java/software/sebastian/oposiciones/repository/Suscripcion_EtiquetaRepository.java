package software.sebastian.oposiciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import software.sebastian.oposiciones.model.Suscripcion_Etiqueta;

@Repository
public interface Suscripcion_EtiquetaRepository
        extends JpaRepository<Suscripcion_Etiqueta, Suscripcion_Etiqueta.PrimaryKey> {

    /**
     * @param suscripcionID
     * @return
     */
    List<Suscripcion_Etiqueta> findBySuscripcionId(Integer suscripcionID);

    @Modifying
    @Query("DELETE FROM Suscripcion_Etiqueta se WHERE se.suscripcionId = :id")
    void deleteBySuscripcionId(@Param("id") Integer id);

}
