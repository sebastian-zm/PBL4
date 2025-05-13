package software.sebastian.oposiciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.sebastian.oposiciones.service.ConvocatoriaService;

@Controller
public class ConvocatoriaController {

    private final ConvocatoriaService service;

    public ConvocatoriaController(ConvocatoriaService service) {
        this.service = service;
    }

    @GetMapping("/convocatorias")
    public String verConvocatorias(Model model) {
        model.addAttribute("convocatorias", service.findAll());
        return "convocatorias";
    }
}
