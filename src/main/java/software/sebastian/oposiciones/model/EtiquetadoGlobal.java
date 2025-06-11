package software.sebastian.oposiciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ETIQUETADO_GLOBAL")
@IdClass(EtiquetadoGlobal.PrimaryKey.class)
public class EtiquetadoGlobal {

    @Id
    @Column(nullable = false)
    private Integer convocatoriaId;

    @Id
    @Column(nullable = false)
    private Integer etiquetaId;

    @Column(nullable = false)
    private Integer valoracion;

    private Integer status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public EtiquetadoGlobal() {
    }

    public EtiquetadoGlobal(Integer convocatoriaId, Integer etiquetaId, Integer valoracion,
            Integer status) {
        this.convocatoriaId = convocatoriaId;
        this.etiquetaId = etiquetaId;
        this.valoracion = valoracion;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getConvocatoriaId() {
        return convocatoriaId;
    }

    public void setConvocatoriaId(Integer convocatoriaId) {
        this.convocatoriaId = convocatoriaId;
    }

    public Integer getEtiquetaId() {
        return etiquetaId;
    }

    public void setEtiquetaId(Integer etiquetaId) {
        this.etiquetaId = etiquetaId;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EtiquetadoGlobal))
            return false;
        EtiquetadoGlobal that = (EtiquetadoGlobal) o;
        return Objects.equals(convocatoriaId, that.convocatoriaId) &&
                Objects.equals(etiquetaId, that.etiquetaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(convocatoriaId, etiquetaId);
    }

    /**
     * Clase de clave primaria compuesta para ETIQUETADO.
     */
    public static class PrimaryKey implements Serializable {
        private Integer convocatoriaId;
        private Integer etiquetaId;

        public PrimaryKey() {
        }

        public PrimaryKey(Integer convocatoriaId, Integer etiquetaId) {
            this.convocatoriaId = convocatoriaId;
            this.etiquetaId = etiquetaId;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof PrimaryKey))
                return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(convocatoriaId, that.convocatoriaId) &&
                    Objects.equals(etiquetaId, that.etiquetaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(convocatoriaId, etiquetaId);
        }
    }
}