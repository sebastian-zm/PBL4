package software.sebastian.oposiciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FEEDBACK")
@IdClass(FeedbackId.class)
public class Feedback {

    @Id
    @Column(name = "usuarioId", nullable = false)
    private Integer usuarioId;

    @Id
    @Column(name = "convocatoriaId", nullable = false)
    private Integer convocatoriaId;

    @Id
    @Column(name = "etiquetaId", nullable = false)
    private Integer etiquetaId;

    @Column(nullable = false)
    private boolean aprobado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 255)
    private String comentario;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    // Relaciones

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "convocatoriaId", referencedColumnName = "convocatoriaId", insertable = false, updatable = false),
        @JoinColumn(name = "etiquetaId", referencedColumnName = "etiquetaId", insertable = false, updatable = false)
    })
    private Etiquetado etiquetado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "convocatoriaId", referencedColumnName = "convocatoriaId", insertable = false, updatable = false),
        @JoinColumn(name = "etiquetaId", referencedColumnName = "etiquetaId", insertable = false, updatable = false)
    })

    private Etiquetado etiquetadoGlobal;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
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

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Etiquetado getEtiquetado() {
        return etiquetado;
    }

    public void setEtiquetado(Etiquetado etiquetado) {
        this.etiquetado = etiquetado;
    }

    public Etiquetado getEtiquetadoGlobal() {
        return etiquetadoGlobal;
    }

    public void setEtiquetadoGlobal(Etiquetado etiquetadoGlobal) {
        this.etiquetadoGlobal = etiquetadoGlobal;
    }    
}
