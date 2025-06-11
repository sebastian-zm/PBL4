package software.sebastian.oposiciones.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.sebastian.oposiciones.service.ConvocatoriaGeneratorService;

@RestController
@RequestMapping("/admin/convocatorias")
@PreAuthorize("hasRole('ADMIN')")
public class ConvocatoriaGeneratorController {

    private final ConvocatoriaGeneratorService convocatoriaGeneratorService;

    public ConvocatoriaGeneratorController(ConvocatoriaGeneratorService convocatoriaGeneratorService) {
        this.convocatoriaGeneratorService = convocatoriaGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateConvocatorias() {
        try {
            convocatoriaGeneratorService.generateConvocatorias();
            return ResponseEntity.ok("Convocatoria generation completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error generating convocatorias: " + e.getMessage());
        }
    }
}