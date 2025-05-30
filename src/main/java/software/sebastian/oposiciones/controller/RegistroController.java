package software.sebastian.oposiciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import software.sebastian.oposiciones.service.UsuarioService;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService svc;

    public RegistroController(UsuarioService svc) {
        this.svc = svc;
    }

    @GetMapping
    public String form() {
        return "registro/formulario";
    }

    @PostMapping
    public String create(@RequestParam String nombre,
                         @RequestParam String apodo,
                         @RequestParam String email,
                         @RequestParam String contrasena,
                         Model model) {
        try {
            svc.create(nombre, apodo, email, contrasena, 1);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registro/formulario";
        }
    }
}
