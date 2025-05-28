package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "MENSAJE")
public class Mensaje {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer mensajeId;

    @ManyToOne
    @JoinColumn(name = "hiloId")
    private Hilo hilo;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    private String contenido;
    private LocalDateTime createdAt;
    public Integer getMensajeId() {
        return mensajeId;
    }
    public void setMensajeId(Integer mensajeId) {
        this.mensajeId = mensajeId;
    }
    public Hilo getHilo() {
        return hilo;
    }
    public void setHilo(Hilo hilo) {
        this.hilo = hilo;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario2) {
        this.usuario = usuario2;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}
