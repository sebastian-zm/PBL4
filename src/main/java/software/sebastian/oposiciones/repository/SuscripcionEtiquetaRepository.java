package software.sebastian.oposiciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import software.sebastian.oposiciones.model.SuscripcionEtiqueta;

@Repository
public interface SuscripcionEtiquetaRepository
        extends JpaRepository<SuscripcionEtiqueta, SuscripcionEtiqueta.PrimaryKey> {

    /**
     * @param suscripcionID
     * @return
     */
    List<SuscripcionEtiqueta> findBySuscripcionId(Integer suscripcionID);

    @Modifying
    @Query("DELETE FROM Suscripcion_Etiqueta se WHERE se.suscripcionId = :id")
    void deleteBySuscripcionId(@Param("id") Integer id);

}
