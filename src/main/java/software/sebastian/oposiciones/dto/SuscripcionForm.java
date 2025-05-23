package software.sebastian.oposiciones.dto;

import java.util.List;

public class SuscripcionForm {

    private List<Integer> etiquetasSeleccionadas;

    public List<Integer> getEtiquetasSeleccionadas() {
        return etiquetasSeleccionadas;
    }

    public void setEtiquetasSeleccionadas(List<Integer> etiquetasSeleccionadas) {
        this.etiquetasSeleccionadas = etiquetasSeleccionadas;
    }
}
