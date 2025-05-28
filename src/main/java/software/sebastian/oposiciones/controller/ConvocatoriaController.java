package software.sebastian.oposiciones.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.service.ConvocatoriaService;
import software.sebastian.oposiciones.service.EtiquetadoService;
import software.sebastian.oposiciones.service.SuscripcionService;
import software.sebastian.oposiciones.service.UsuarioService;

@Controller
public class ConvocatoriaController {

    private final ConvocatoriaService service;

    private final UsuarioService usService;
    private final SuscripcionService serviceSus;
    private final EtiquetadoService etiquetadoSe;

    public ConvocatoriaController(ConvocatoriaService service, UsuarioService usService,
            SuscripcionService serviceSus, EtiquetadoService etiquetadoSe) {
        this.service = service;
        this.usService = usService;
        this.serviceSus = serviceSus;
        this.etiquetadoSe = etiquetadoSe;
    }
@GetMapping("/convocatorias")
public String verConvocatorias(Model model, Principal principal) {
    model.addAttribute("etiquetasPorConvocatoria", etiquetadoSe.getEtiquetasPorConvocatoria());
    model.addAttribute("convocatorias", service.findAll());

    if (principal != null) {
        Usuario usuario = usService.getCurrentUser(principal);
        model.addAttribute("suscripciones_usuario",
                serviceSus.findSusByUser(usuario.getUsuarioId()));
    }

    return "convocatorias";
}

   @GetMapping("/convocatorias/suscripcion")
public String verConvocatoriasPorSuscripcion(
        @RequestParam(value = "suscripcionId", required = false) String suscripcionIdStr,
        Model model, Principal principal) {
    Usuario usuario = usService.getCurrentUser(principal);
    model.addAttribute("suscripciones_usuario", serviceSus.findSusByUser(usuario.getUsuarioId()));

    if ("todas".equals(suscripcionIdStr)) {
        model.addAttribute("convocatorias", service.findConvocatoriasByTodasLasSuscripciones(usuario.getUsuarioId()));
    } else if (suscripcionIdStr != null && !suscripcionIdStr.isEmpty()) {
        Integer suscripcionId = Integer.valueOf(suscripcionIdStr);
        model.addAttribute("convocatorias", service.findConvocatoriasBySuscripcion(suscripcionId));
    } else {
        model.addAttribute("convocatorias", service.findAll());
    }

    model.addAttribute("etiquetasPorConvocatoria", etiquetadoSe.getEtiquetasPorConvocatoria());
    return "convocatorias";
}

}
