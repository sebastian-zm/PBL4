package software.sebastian.oposiciones.dto;

import java.util.List;

public class SuscripcionForm {

    private String nombre;
    private List<Integer> etiquetasSeleccionadas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Integer> getEtiquetasSeleccionadas() {
        return etiquetasSeleccionadas;
    }

    public void setEtiquetasSeleccionadas(List<Integer> etiquetasSeleccionadas) {
        this.etiquetasSeleccionadas = etiquetasSeleccionadas;
    }
}
