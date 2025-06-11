package software.sebastian.oposiciones.model;

import java.io.Serializable;
import java.util.Objects;

public class FeedbackId implements Serializable {
    private Integer usuarioId;
    private Integer convocatoriaId;
    private Integer etiquetaId;

    public FeedbackId() {}

    public FeedbackId(Integer usuarioId, Integer convocatoriaId, Integer etiquetaId) {
        this.usuarioId = usuarioId;
        this.convocatoriaId = convocatoriaId;
        this.etiquetaId = etiquetaId;
    }

    // equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedbackId)) return false;
        FeedbackId that = (FeedbackId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
            Objects.equals(convocatoriaId, that.convocatoriaId) &&
            Objects.equals(etiquetaId, that.etiquetaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, convocatoriaId, etiquetaId);
    }
}