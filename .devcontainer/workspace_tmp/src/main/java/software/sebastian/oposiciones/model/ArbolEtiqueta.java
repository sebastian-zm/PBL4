package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARBOL_ETIQUETAS")
public class ArbolEtiqueta {

    @EmbeddedId
    private ArbolEtiquetaId id;

    @MapsId("ancestroId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ancestroId")
    private Etiqueta ancestro;

    @MapsId("descendienteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "descendienteId")
    private Etiqueta descendiente;

    @Column(nullable = false)
    private Integer distancia;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ArbolEtiqueta() {}

    public ArbolEtiqueta(Etiqueta a, Etiqueta d, Integer dist) {
        this.ancestro = a;
        this.descendiente = d;
        this.distancia = dist;
        this.id = new ArbolEtiquetaId(a.getEtiquetaId(), d.getEtiquetaId());
    }

    // getters & setters
    public ArbolEtiquetaId getId() { return id; }
    public Etiqueta getAncestro() { return ancestro; }
    public Etiqueta getDescendiente() { return descendiente; }
    public Integer getDistancia() { return distancia; }
}
