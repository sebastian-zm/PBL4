package software.sebastian.oposiciones.controller;

import java.security.Principal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.UsuarioRepository;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/perfil")
    public String mostrarFormularioPerfil(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuario/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String apodo,
                                   @RequestParam String email,
                                   Principal principal,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Authentication auth) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar apodo
        if (!usuario.getApodo().equals(apodo) && usuarioRepository.findByApodo(apodo).isPresent()) {
            model.addAttribute("error", "El apodo ya está en uso.");
            model.addAttribute("usuario", usuario);
            return "usuario/perfil";
        }

        // Validar email
        if (!usuario.getEmail().equals(email) && usuarioRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El correo electrónico ya está en uso.");
            model.addAttribute("usuario", usuario);
            return "usuario/perfil";
        }

        boolean emailHaCambiado = !usuario.getEmail().equals(email);

        usuario.setNombre(nombre);
        usuario.setApodo(apodo);
        usuario.setEmail(email);
        usuarioRepository.save(usuario);

        if (emailHaCambiado) {
            // Invalidamos la sesión y el contexto de seguridad
            new SecurityContextLogoutHandler().logout(request, response, auth);
            // Redirigimos al login con mensaje de correo cambiado
            return "redirect:/login?logout&msg=email-cambiado";
        }

        model.addAttribute("success", "Perfil actualizado correctamente.");
        model.addAttribute("usuario", usuario);
        return "usuario/perfil";
    }
}
