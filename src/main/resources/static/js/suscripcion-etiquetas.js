document.addEventListener('DOMContentLoaded', function() {
    // Convertir el mapa de relaciones de Thymeleaf a JavaScript
    const relacionesPadreHijo = window.relacionesPadreHijo || {};

    const checkboxes = document.querySelectorAll('.etiqueta-checkbox');

    // Función para deshabilitar/habilitar hijos directos
    function toggleHijosDirectos(padreId, deshabilitar) {
        if (relacionesPadreHijo[padreId]) {
            relacionesPadreHijo[padreId].forEach(hijoId => {
                const hijoCheckbox = document.querySelector(`.etiqueta-checkbox[data-id="${hijoId}"]`);
                if (hijoCheckbox) {
                    hijoCheckbox.disabled = deshabilitar;
                    if (deshabilitar) {
                        hijoCheckbox.checked = false;
                        hijoCheckbox.closest('.form-check').classList.add('disabled');
                    } else {
                        hijoCheckbox.closest('.form-check').classList.remove('disabled');
                        // Verificar si este hijo no tiene otro padre marcado
                        const tieneOtroPadreMarcado = Object.entries(relacionesPadreHijo).some(([pid, hijos]) => 
                            pid != padreId && hijos.includes(hijoId) && 
                            document.querySelector(`.etiqueta-checkbox[data-id="${pid}"]`)?.checked
                        );
                        if (!tieneOtroPadreMarcado) {
                            hijoCheckbox.disabled = false;
                        }
                    }
                }
            });
        }
    }

    checkboxes.forEach(checkbox => {
        const etiquetaId = parseInt(checkbox.getAttribute('data-id'));

        checkbox.addEventListener('change', function() {
            toggleHijosDirectos(etiquetaId, this.checked);
        });

        // Inicializar estado basado en padres marcados
        const tienePadreMarcado = Object.entries(relacionesPadreHijo).some(([pid, hijos]) => 
            hijos.includes(etiquetaId) && 
            document.querySelector(`.etiqueta-checkbox[data-id="${pid}"]`)?.checked
        );

        if (tienePadreMarcado) {
            checkbox.disabled = true;
            checkbox.closest('.form-check').classList.add('disabled');
        }
    });
});
