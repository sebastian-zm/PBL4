package software.sebastian.oposiciones.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class ArbolEtiquetaId implements Serializable {
    private Integer ancestroId;
    private Integer descendienteId;

    public ArbolEtiquetaId() {}
    public ArbolEtiquetaId(Integer a, Integer d) {
        this.ancestroId = a;
        this.descendienteId = d;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArbolEtiquetaId)) return false;
        ArbolEtiquetaId that = (ArbolEtiquetaId) o;
        return Objects.equals(ancestroId, that.ancestroId)
            && Objects.equals(descendienteId, that.descendienteId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ancestroId, descendienteId);
    }
}
