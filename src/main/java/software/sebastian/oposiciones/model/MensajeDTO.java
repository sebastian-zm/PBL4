package software.sebastian.oposiciones.model;

public class MensajeDTO {
    private Integer hiloId;
    private String contenido;
    private String usuarioNombre;
    private String createdAt;

    public Integer getHiloId() { return hiloId; }
    public void setHiloId(Integer hiloId) { this.hiloId = hiloId; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
