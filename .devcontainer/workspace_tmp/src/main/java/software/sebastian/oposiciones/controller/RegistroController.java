package software.sebastian.oposiciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import software.sebastian.oposiciones.service.UsuarioService;

@Controller
@RequestMapping("/registro")
public class RegistroController {
    private final UsuarioService svc;

    public RegistroController(UsuarioService svc) { this.svc = svc; }


    @GetMapping
    public String tree() {
        return "registro/formulario";
    }

    @PostMapping
    public String create(@RequestParam String nombre,
                         @RequestParam String email,
                         @RequestParam String contrasena) {
        svc.create(nombre, email, contrasena, 1);
        System.out.println(nombre + " " + email + " " + contrasena + " ");
        return "redirect:/login";
    }
}
