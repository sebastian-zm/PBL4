package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "HILO")
public class Hilo {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer hiloId;

    private String titulo;

    @ManyToOne
    @JoinColumn(name = "creadorId")
    private Usuario creador;

    @OneToMany(mappedBy = "hilo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Mensaje> mensajes = new ArrayList<>();

    private LocalDateTime createdAt;

    public Integer getHiloId() {
        return hiloId;
    }

    public void setHiloId(Integer hiloId) {
        this.hiloId = hiloId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
