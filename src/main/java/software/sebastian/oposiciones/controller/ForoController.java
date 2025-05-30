package software.sebastian.oposiciones.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import software.sebastian.oposiciones.model.Hilo;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.HiloRepository;
import software.sebastian.oposiciones.repository.MensajeRepository;
import software.sebastian.oposiciones.repository.UsuarioRepository;

@Controller
@RequestMapping("/foro")
public class ForoController {

    @Autowired
    private HiloRepository hiloRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping("/crear")
    public String mostrarFormularioHilo(Model model) {
        model.addAttribute("hilo", new Hilo());
        return "hilos/crear";
    }

    @PostMapping("/crear")
    public String procesarNuevoHilo(@ModelAttribute Hilo hilo, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        hilo.setCreador(usuario);
        hilo.setCreatedAt(LocalDateTime.now());
        hiloRepository.save(hilo);

        return "redirect:/foro";
    }

    @GetMapping("/{hiloId}")
    public String verHilo(@PathVariable Integer hiloId, Model model, Principal principal) {
        Hilo hilo = hiloRepository.findById(hiloId)
            .orElseThrow(() -> new RuntimeException("Hilo no encontrado"));

        model.addAttribute("hilo", hilo);
        model.addAttribute("mensajes", mensajeRepository.findByHilo_HiloIdOrderByCreatedAtAsc(hiloId));

        // ✅ Añadir apodo del usuario autenticado
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuarioApodo", usuario.getApodo());

        return "hilos/hilo";
    }

    @GetMapping
    public String listarHilos(Model model) {
        model.addAttribute("hilos", hiloRepository.findAll());
        return "hilos/hilos"; // plantilla en templates/hilos/hilos.html
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrar/{hiloId}")
    public String borrarHilo(@PathVariable Integer hiloId) {
        hiloRepository.deleteById(hiloId);
        return "redirect:/foro";
    }
}

