package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ETIQUETA")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer etiquetaId;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Se usa para formatear el nombre en embeddings
    @Column
    private String formato; // Formato para el nombre de la etiqueta en embeddings

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Closure‐table links
    @OneToMany(mappedBy = "ancestro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArbolEtiqueta> descendientes = new HashSet<>();

    @OneToMany(mappedBy = "descendiente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArbolEtiqueta> ancestros = new HashSet<>();

    // getters & setters

    public Integer getEtiquetaId() { return etiquetaId; }
    public void setEtiquetaId(Integer etiquetaId) { this.etiquetaId = etiquetaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Set<ArbolEtiqueta> getDescendientes() { return descendientes; }
    public Set<ArbolEtiqueta> getAncestros() { return ancestros; }
}
