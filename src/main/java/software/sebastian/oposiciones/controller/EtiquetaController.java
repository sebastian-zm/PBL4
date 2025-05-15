package software.sebastian.oposiciones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import software.sebastian.oposiciones.service.EtiquetaService;
import software.sebastian.oposiciones.service.EtiquetaService.TreeNode;

@Controller
@RequestMapping("/etiquetas")
public class EtiquetaController {

    private final EtiquetaService svc;
    public EtiquetaController(EtiquetaService svc) { this.svc = svc; }

    @GetMapping
    public String tree(Model m) {
        List<TreeNode> forest = svc.getTree();
        m.addAttribute("forest", forest);
        return "etiquetas/tree";
    }

    @PostMapping
    public String create(@RequestParam String nombre,
                         @RequestParam(required=false) String formato,
                         @RequestParam(required=false) Integer parentId) {
        svc.createWithParent(nombre, formato, parentId);
        return "redirect:/etiquetas";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id,
                       @RequestParam String nombre,
                       @RequestParam(required=false) String formato) {
        svc.update(id, nombre, formato);
        return "redirect:/etiquetas";
    }

    @PostMapping("/{id}/move")
    @ResponseBody
    public Map<String,String> move(@PathVariable Integer id,
            @RequestParam(required=false) Integer parentId) {
      svc.moveSubtree(id, parentId);
      return Map.of("status","ok");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        svc.delete(id);
        return "redirect:/etiquetas";
    }
}
