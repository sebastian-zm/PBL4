package software.sebastian.oposiciones.model;

public class MensajeDTO {
    private Integer hiloId;
    private String contenido;
    private String usuarioApodo;
    private String createdAt;

    public Integer getHiloId() {
        return hiloId;
    }

    public void setHiloId(Integer hiloId) {
        this.hiloId = hiloId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUsuarioApodo() {
        return usuarioApodo;
    }

    public void setUsuarioApodo(String usuarioApodo) {
        this.usuarioApodo = usuarioApodo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
