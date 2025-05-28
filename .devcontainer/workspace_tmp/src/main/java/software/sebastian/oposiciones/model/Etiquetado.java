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
@Table(name = "ETIQUETADO")
@IdClass(Etiquetado.PrimaryKey.class)
public class Etiquetado {

    @Id
    @Column(nullable = false)
    private Integer convocatoriaId;

    @Id
    @Column(nullable = false)
    private Integer etiquetaId;

    @Id
    @Column(nullable = false)
    private Integer modeloId;

    private Integer valoracion;

    private Double confianza;

    private Integer status;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public Etiquetado() {
    }

    public Etiquetado(Integer convocatoriaId, Integer etiquetaId, Integer modeloId,
                      Integer valoracion, Double confianza) {
        this.convocatoriaId = convocatoriaId;
        this.etiquetaId = etiquetaId;
        this.modeloId = modeloId;
        this.valoracion = valoracion;
        this.confianza = confianza;
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

    public Integer getModeloId() {
        return modeloId;
    }

    public void setModeloId(Integer modeloId) {
        this.modeloId = modeloId;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Double getConfianza() {
        return confianza;
    }

    public void setConfianza(Double confianza) {
        this.confianza = confianza;
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
        if (this == o) return true;
        if (!(o instanceof Etiquetado)) return false;
        Etiquetado that = (Etiquetado) o;
        return Objects.equals(convocatoriaId, that.convocatoriaId) &&
               Objects.equals(etiquetaId, that.etiquetaId) &&
               Objects.equals(modeloId, that.modeloId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(convocatoriaId, etiquetaId, modeloId);
    }

    /**
     * Clase de clave primaria compuesta para ETIQUETADO.
     */
    public static class PrimaryKey implements Serializable {
        private Integer convocatoriaId;
        private Integer etiquetaId;
        private Integer modeloId;

        public PrimaryKey() {
        }

        public PrimaryKey(Integer convocatoriaId, Integer etiquetaId, Integer modeloId) {
            this.convocatoriaId = convocatoriaId;
            this.etiquetaId = etiquetaId;
            this.modeloId = modeloId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PrimaryKey)) return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(convocatoriaId, that.convocatoriaId) &&
                   Objects.equals(etiquetaId, that.etiquetaId) &&
                   Objects.equals(modeloId, that.modeloId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(convocatoriaId, etiquetaId, modeloId);
        }
    }
}