package software.sebastian.oposiciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import software.sebastian.oposiciones.service.BackupService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    /**
     * Vista de gestión de copias de seguridad (HTML)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/gestion")
    public String mostrarVistaGestion() {
        return "backup/gestion"; // → templates/backup/gestion.html
    }

    /**
     * Crear una nueva copia de seguridad (REST: GET /backup)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @GetMapping
    public ResponseEntity<String> hacerBackup() {
        try {
            String resultado = backupService.crearBackup();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al crear backup: " + e.getMessage());
        }
    }

    /**
     * Listar archivos de copia disponibles (REST: GET /backup/copias)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @GetMapping("/copias")
    public ResponseEntity<List<String>> listarCopias() {
        try {
            List<String> copias = backupService.listarCopiasDisponibles();
            return ResponseEntity.ok(copias);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("❌ Error al listar copias: " + e.getMessage()));
        }
    }

    /**
     * Restaurar una copia seleccionada (REST: POST /backup/restaurar)
     * JSON: { "archivo": "nombre.sql" }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @PostMapping("/restaurar")
    public ResponseEntity<String> restaurarCopia(@RequestBody Map<String, String> body) {
        String archivo = body.get("archivo");

        if (archivo == null || archivo.isBlank()) {
            return ResponseEntity.badRequest().body("⚠️ El nombre del archivo es requerido.");
        }

        try {
            String resultado = backupService.restaurarCopia(archivo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al restaurar: " + e.getMessage());
        }
    }

    /**
     * Borrar una copia seleccionada (REST: POST /backup/borrar)
     * JSON: { "archivo": "nombre.sql" }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @PostMapping("/borrar")
    public ResponseEntity<String> borrarCopia(@RequestBody Map<String, String> body) {
        String archivo = body.get("archivo");

        if (archivo == null || archivo.isBlank()) {
            return ResponseEntity.badRequest().body("⚠️ El nombre del archivo es requerido.");
        }

        try {
            String resultado = backupService.borrarCopia(archivo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al borrar copia: " + e.getMessage());
        }
    }
}
