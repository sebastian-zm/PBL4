package software.sebastian.oposiciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import software.sebastian.oposiciones.repository.MensajeRepository;

@Controller
@RequestMapping("/mensajes")
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrar/{id}")
    public String borrarMensaje(@PathVariable Integer id, @RequestHeader("Referer") String referer) {
        mensajeRepository.deleteById(id);
        return "redirect:" + referer;
    }
}