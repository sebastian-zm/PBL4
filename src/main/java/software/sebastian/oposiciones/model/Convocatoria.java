package software.sebastian.oposiciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONVOCATORIA")
public class Convocatoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer convocatoriaId;

    @Column(nullable = false, unique = true)
    private String boeId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    @Column(nullable = false)
    private String enlace;

    public Convocatoria() {
    }

    public Integer getConvocatoriaId() {
        return convocatoriaId;
    }

    public void setConvocatoriaId(Integer convocatoriaId) {
        this.convocatoriaId = convocatoriaId;
    }

    public String getBoeId() {
        return boeId;
    }

    public void setBoeId(String boeId) {
        this.boeId = boeId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }
}