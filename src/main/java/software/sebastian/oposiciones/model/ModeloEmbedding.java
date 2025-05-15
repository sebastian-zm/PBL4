package software.sebastian.oposiciones.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad para la tabla MODELO_EMBEDDING.
 * Guarda el embedding JSON (como LONGBLOB) y gestiona su serialización/deserialización.
 * Ahora soporta entidad polimórfica con entidadId y entidadTipo.
 */
@Entity
@Table(name = "MODELO_EMBEDDING")
@IdClass(ModeloEmbedding.PrimaryKey.class)
public class ModeloEmbedding {

    @Id
    @Column(nullable = false)
    private Integer modeloId;

    @Id
    @Column(nullable = false)
    private Integer entidadId;

    @Id
    @Column(nullable = false, length = 50)
    private String entidadTipo;

    /**
     * JSON del vector de embedding en bytes (LONGBLOB)
     */
    @Lob
    @Column(name = "embedding", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] embeddingBlob;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ModeloEmbedding() {
    }

    public ModeloEmbedding(Integer modeloId, Integer entidadId, String entidadTipo, double[] embedding) {
        this.modeloId = modeloId;
        this.entidadId = entidadId;
        this.entidadTipo = entidadTipo;
        setEmbedding(embedding);
    }

    public Integer getModeloId() {
        return modeloId;
    }

    public void setModeloId(Integer modeloId) {
        this.modeloId = modeloId;
    }

    public Integer getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Integer entidadId) {
        this.entidadId = entidadId;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public void setEntidadTipo(String entidadTipo) {
        this.entidadTipo = entidadTipo;
    }

    /**
     * Obtiene el embedding como array de double, parseando el JSON almacenado.
     */
    @Transient
    public double[] getEmbedding() {
        try {
            return MAPPER.readValue(embeddingBlob, double[].class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializando embedding JSON", e);
        }
    }

    /**
     * Serializa el vector de double a JSON y lo almacena en el blob.
     */
    public void setEmbedding(double[] embedding) {
        try {
            this.embeddingBlob = MAPPER.writeValueAsBytes(embedding);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando embedding JSON", e);
        }
    }

    public byte[] getEmbeddingBlob() {
        return embeddingBlob;
    }

    public void setEmbeddingBlob(byte[] embeddingBlob) {
        this.embeddingBlob = embeddingBlob;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Clave primaria compuesta para ModeloEmbedding.
     */
    public static class PrimaryKey implements Serializable {
        private Integer modeloId;
        private Integer entidadId;
        private String entidadTipo;

        public PrimaryKey() {
        }

        public PrimaryKey(Integer modeloId, Integer entidadId, String entidadTipo) {
            this.modeloId = modeloId;
            this.entidadId = entidadId;
            this.entidadTipo = entidadTipo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PrimaryKey)) return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(modeloId, that.modeloId) &&
                   Objects.equals(entidadId, that.entidadId) &&
                   Objects.equals(entidadTipo, that.entidadTipo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modeloId, entidadId, entidadTipo);
        }
    }
}
