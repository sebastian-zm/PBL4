package software.sebastian.oposiciones.controller;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.sebastian.oposiciones.dto.FeedbackDto;
import software.sebastian.oposiciones.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

  @Autowired
  private FeedbackService feedbackService;

  @PostMapping
  public ResponseEntity<Map<String,String>> upsertFeedback(@RequestBody FeedbackDto dto) {
    try {
      feedbackService.upsert(dto.getUsuarioId(), dto.getConvocatoriaId(), dto.getEtiquetaId(), dto.isAprobado());

      return ResponseEntity.ok(
        Collections.singletonMap("message", "Feedback registrado correctamente")
      );

    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonMap("error", e.getMessage()));
    }
  }

  @GetMapping("/ya-evaluado")
  public ResponseEntity<Boolean> yaEvaluado(
      @RequestParam int usuarioId,
      @RequestParam int convocatoriaId,
      @RequestParam int etiquetaId) {

      boolean exists = feedbackService.existeFeedback(usuarioId, convocatoriaId, etiquetaId);
      return ResponseEntity.ok(exists);
  }
}
