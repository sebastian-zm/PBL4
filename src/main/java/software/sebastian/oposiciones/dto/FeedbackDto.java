package software.sebastian.oposiciones.dto;

public class FeedbackDto {
    private int usuarioId;
    private int convocatoriaId;
    private int etiquetaId;
    private boolean aprobado;

    public FeedbackDto() {}

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getConvocatoriaId() {
        return convocatoriaId;
    }

    public void setConvocatoriaId(int convocatoriaId) {
        this.convocatoriaId = convocatoriaId;
    }

    public int getEtiquetaId() {
        return etiquetaId;
    }

    public void setEtiquetaId(int etiquetaId) {
        this.etiquetaId = etiquetaId;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
}
